package com.kavi.kavimart.exception;

/** Business-state conflict mapped to HTTP 409. */
public class ConflictException extends AppException {
  private static final long serialVersionUID = 1L;

  /**
   * Constructs conflict exception.
   *
   * @param message failure message
   */
  public ConflictException(String message) {
    super("CONFLICT", message, 409);
  }
}
