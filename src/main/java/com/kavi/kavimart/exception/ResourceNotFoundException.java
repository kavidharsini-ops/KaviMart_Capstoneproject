package com.kavi.kavimart.exception;

/** Resource lookup failure mapped to HTTP 404. */
public class ResourceNotFoundException extends AppException {
  private static final long serialVersionUID = 1L;

  /**
   * Constructs resource not found exception.
   *
   * @param message failure message
   */
  public ResourceNotFoundException(String message) {
    super("NOT_FOUND", message, 404);
  }
}
