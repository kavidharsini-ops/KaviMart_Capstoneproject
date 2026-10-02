package com.kavi.kavimart.exception;

import java.util.Map;
/** Validation failure with field-level messages. */
public class ValidationException extends AppException {
  private final Map<String,String> fields;
  public ValidationException(Map<String,String> fields) { super("VALIDATION_ERROR", fields.values().stream().findFirst().orElse("Please check the submitted values."), 400); this.fields=Map.copyOf(fields); }
  public Map<String,String> getFields() { return fields; }
}
