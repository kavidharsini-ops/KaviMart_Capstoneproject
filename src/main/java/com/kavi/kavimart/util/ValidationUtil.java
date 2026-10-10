package com.kavi.kavimart.util;

import com.kavi.kavimart.exception.ValidationException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** Shared input validation rules used at service boundaries. */
public final class ValidationUtil {
  private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

  private ValidationUtil() { }

  /** Records an error message when valid condition is false. */
  public static void require(boolean valid, String field, String message,
      Map<String, String> errors) {
    if (!valid) {
      errors.put(field, message);
    }
  }

  /** Throws ValidationException if any errors were collected. */
  public static void throwIfAny(Map<String, String> errors) {
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  /** Creates a new errors map. */
  public static Map<String, String> errors() {
    return new LinkedHashMap<>();
  }

  /** Checks if an email is well-formed and within length limit. */
  public static boolean validEmail(String email) {
    return email != null && email.length() <= 150 && EMAIL.matcher(email.trim()).matches();
  }

  /** Checks if a password meets minimum and maximum length bounds. */
  public static boolean validPassword(String password) {
    return password != null && password.length() >= 8 && password.length() <= 72;
  }

  /** Checks if a price is non-negative and properly scaled. */
  public static boolean validPrice(BigDecimal price) {
    return price != null && price.signum() >= 0 && price.scale() <= 2 && price.precision() <= 10;
  }
}
