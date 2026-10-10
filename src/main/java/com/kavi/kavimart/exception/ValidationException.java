package com.kavi.kavimart.exception;

import java.util.Map;

/** Validation failure with field-level messages. */
public class ValidationException extends AppException {
  private static final long serialVersionUID = 1L;

  private final Map<String, String> fields;

  /**
   * Constructs validation exception with field-level errors.
   *
   * @param fields map of field names to error messages
   */
  public ValidationException(Map<String, String> fields) {
    super("VALIDATION_ERROR",
        fields.values().stream().findFirst().orElse("Please check the submitted values."),
        400);
    this.fields = Map.copyOf(fields);
  }

  /**
   * Returns field validation errors.
   *
   * @return immutable map of field names to error messages
   */
  public Map<String, String> getFields() {
    return fields;
  }
}
