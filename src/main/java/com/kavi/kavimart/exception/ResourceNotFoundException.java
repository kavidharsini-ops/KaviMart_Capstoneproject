package com.kavi.kavimart.exception;
/** Resource lookup failure mapped to HTTP 404. */
public class ResourceNotFoundException extends AppException { public ResourceNotFoundException(String message) { super("NOT_FOUND", message, 404); } }
