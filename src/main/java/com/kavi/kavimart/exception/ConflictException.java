package com.kavi.kavimart.exception;
/** Business-state conflict mapped to HTTP 409. */
public class ConflictException extends AppException { public ConflictException(String message) { super("CONFLICT", message, 409); } }
