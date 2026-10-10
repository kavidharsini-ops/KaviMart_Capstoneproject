package com.kavi.kavimart.util;

import org.mindrot.jbcrypt.BCrypt;

/** BCrypt password hashing and verification. Passwords are never logged. */
public final class PasswordUtil {

  private PasswordUtil() { }

  /**
   * Hashes a plain password using BCrypt with log rounds 12.
   *
   * @param password raw password string
   * @return BCrypt hash
   */
  public static String hash(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt(12));
  }

  /**
   * Verifies a plain password against an existing BCrypt hash.
   *
   * @param password raw password string
   * @param hash stored BCrypt hash
   * @return true if password matches hash
   */
  public static boolean matches(String password, String hash) {
    return password != null && hash != null && BCrypt.checkpw(password, hash);
  }
}
