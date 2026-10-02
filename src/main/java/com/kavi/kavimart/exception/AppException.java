package com.kavi.kavimart.exception;

/** Base exception carrying an HTTP-compatible error code and status. */
public class AppException extends RuntimeException {
  private final int status;
  private final String code;
  public AppException(String code, String message, int status) { super(message); this.code=code; this.status=status; }
  public int getStatus() { return status; }
  public String getCode() { return code; }
}
