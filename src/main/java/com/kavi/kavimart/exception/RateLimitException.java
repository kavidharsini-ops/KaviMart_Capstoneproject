package com.kavi.kavimart.exception;

/** Raised when a visitor sends chat messages too quickly (HTTP 429). */
public class RateLimitException extends AppException {

  /** Creates the exception with the standard friendly message. */
  public RateLimitException() {
    super("RATE_LIMITED",
        "You are sending messages too quickly. Please wait a minute and try again.", 429);
  }
}