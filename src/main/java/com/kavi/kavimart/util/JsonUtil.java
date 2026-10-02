package com.kavi.kavimart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.kavi.kavimart.dto.ApiEnvelope;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/** JSON envelope serializer shared by API endpoints. */
public final class JsonUtil {
  private static final Gson GSON = new GsonBuilder()
      .disableHtmlEscaping()
      .registerTypeAdapter(LocalDateTime.class,
          (JsonSerializer<LocalDateTime>) (value, type, context) ->
              value == null ? JsonNull.INSTANCE : new JsonPrimitive(value.toString()))
      .create();

  private JsonUtil() { }

  public static void success(HttpServletResponse response, int status, Object data)
      throws IOException {
    write(response, status, new ApiEnvelope(true, data, null));
  }

  public static void error(HttpServletResponse response, int status, String code, String message,
      Map<String, String> fields) throws IOException {
    write(response, status, new ApiEnvelope(false, null,
        new ApiEnvelope.ApiError(code, message, fields)));
  }

  private static void write(HttpServletResponse response, int status, Object value)
      throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    GSON.toJson(value, response.getWriter());
  }

  public static Gson gson() {
    return GSON;
  }
}
