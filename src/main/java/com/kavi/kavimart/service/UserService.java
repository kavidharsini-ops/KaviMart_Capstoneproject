package com.kavi.kavimart.service;

import com.kavi.kavimart.dao.UserDao;
import com.kavi.kavimart.dto.UserResponseDTO;
import com.kavi.kavimart.exception.AppException;
import com.kavi.kavimart.exception.ResourceNotFoundException;
import com.kavi.kavimart.model.User;
import com.kavi.kavimart.util.PasswordUtil;
import com.kavi.kavimart.util.ValidationUtil;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Service managing user registration, authentication, and administrative accounts.
 */
public class UserService {
  private final UserDao dao;

  /**
   * Constructs the user service.
   *
   * @param dao user persistence DAO
   */
  public UserService(UserDao dao) {
    this.dao = dao;
  }

  /**
   * Registers a new user account with BCrypt password hashing and role validation.
   *
   * @param name full name
   * @param email email address
   * @param password raw password
   * @param role account role ("BUYER" or "SELLER")
   * @return safe UserResponseDTO without password data
   */
  public UserResponseDTO register(String name, String email, String password, String role) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(name != null && !name.trim().isEmpty() && name.trim().length() <= 100,
        "name", "Name is required (100 characters maximum).", e);
    ValidationUtil.require(ValidationUtil.validEmail(email),
        "email", "Enter a valid email address.", e);
    ValidationUtil.require(ValidationUtil.validPassword(password),
        "password", "Password must be at least 8 characters.", e);
    String normalizedRole = role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
    ValidationUtil.require(Set.of("BUYER", "SELLER").contains(normalizedRole),
        "role", "Choose buyer or seller.", e);
    ValidationUtil.throwIfAny(e);

    User user = new User(0, name.trim(), email.trim().toLowerCase(Locale.ROOT),
        PasswordUtil.hash(password), normalizedRole, true);
    User saved = dao.create(user);
    return dto(saved);
  }

  /**
   * Authenticates user credentials using secure BCrypt comparison and active flag verification.
   *
   * @param email email address
   * @param password plain text password
   * @return safe UserResponseDTO on successful authentication
   */
  public UserResponseDTO login(String email, String password) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(ValidationUtil.validEmail(email),
        "email", "Enter a valid email address.", e);
    ValidationUtil.require(password != null && !password.isBlank(),
        "password", "Password is required.", e);
    ValidationUtil.throwIfAny(e);

    User user = dao.findByEmail(email.trim().toLowerCase(Locale.ROOT))
        .orElseThrow(() -> new AppException("UNAUTHORIZED",
            "Email or password is incorrect.", 401));

    if (!user.isActive() || !PasswordUtil.matches(password, user.getPasswordHash())) {
      throw new AppException("UNAUTHORIZED", "Email or password is incorrect.", 401);
    }

    return dto(user);
  }

  /**
   * Retrieves all user accounts formatted as safe DTOs for administrative console.
   *
   * @return list of user response DTOs
   */
  public List<UserResponseDTO> listUsers() {
    return dao.findAll().stream().map(UserService::dto).toList();
  }

  /**
   * Activates or deactivates an account.
   *
   * @param userId the user ID
   * @param active desired active state
   */
  public void setActive(long userId, boolean active) {
    Map<String, String> e = ValidationUtil.errors();
    ValidationUtil.require(userId > 0, "userId", "A valid user is required.", e);
    ValidationUtil.throwIfAny(e);
    if (!dao.setActive(userId, active)) {
      throw new ResourceNotFoundException("Account not found.");
    }
  }

  private static UserResponseDTO dto(User u) {
    return new UserResponseDTO(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.isActive());
  }
}
