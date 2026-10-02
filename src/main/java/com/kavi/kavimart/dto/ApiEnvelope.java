package com.kavi.kavimart.dto;
import java.util.Map;
/** Standard JSON API success and error envelope. */
public final class ApiEnvelope {
  private final boolean success; private final Object data; private final ApiError error;
  public ApiEnvelope(boolean success,Object data,ApiError error) { this.success=success;this.data=data;this.error=error; }
  public boolean isSuccess() { return success; } public Object getData() { return data; } public ApiError getError() { return error; }
  public static final class ApiError { private final String code; private final String message; private final Map<String,String> fields;
    public ApiError(String code,String message,Map<String,String> fields) { this.code=code;this.message=message;this.fields=fields; }
    public String getCode(){return code;} public String getMessage(){return message;} public Map<String,String> getFields(){return fields;}
  }
}
