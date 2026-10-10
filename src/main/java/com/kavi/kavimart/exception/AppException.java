package com.kavi.kavimart.exception;

/**
 * Base application runtime exception carrying an HTTP status code and machine error code.
 */
public class AppException extends RuntimeException {
  private final int status;
  private final String code;

  /**
   * Constructs an AppException with a code, message, and HTTP status.
   *
   * @param code error code string
   * @param message descriptive message
   * @param status HTTP response status code
   */
  public AppException(String code, String message, int status) {
    super(message);
    this.code = code;
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }
}
