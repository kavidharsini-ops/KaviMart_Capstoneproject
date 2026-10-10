package com.kavi.kavimart.dao;

import com.kavi.kavimart.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for user accounts and authentication.
 */
public interface UserDao {

  /**
   * Finds an account by normalized email address.
   *
   * @param email normalized lowercase email
   * @return optional containing the user if found
   */
  Optional<User> findByEmail(String email);

  /**
   * Finds an account by primary key ID.
   *
   * @param id the user ID
   * @return optional containing the user if found
   */
  Optional<User> findById(long id);

  /**
   * Creates a new user account with initial active status.
   *
   * @param user user entity to persist
   * @return the created user with its generated ID
   */
  User create(User user);

  /**
   * Retrieves all user accounts in reverse chronological order.
   *
   * @return list of all users
   */
  List<User> findAll();

  /**
   * Sets the active status flag for an account (soft deactivate / activate).
   *
   * @param id the user ID
   * @param active new active state
   * @return true if updated, false if account not found
   */
  boolean setActive(long id, boolean active);

  /**
   * Counts the total number of user accounts.
   *
   * @return total account count
   */
  long count();
}
