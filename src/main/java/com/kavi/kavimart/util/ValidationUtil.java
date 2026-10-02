package com.kavi.kavimart.util;

import com.kavi.kavimart.exception.ValidationException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** Shared input validation rules used at service boundaries. */
public final class ValidationUtil {
  private static final Pattern EMAIL=Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
  private ValidationUtil() { }
  public static void require(boolean valid, String field, String message, Map<String,String> errors) { if (!valid) errors.put(field,message); }
  public static void throwIfAny(Map<String,String> errors) { if (!errors.isEmpty()) throw new ValidationException(errors); }
  public static Map<String,String> errors() { return new LinkedHashMap<>(); }
  public static boolean validEmail(String email) { return email!=null && email.length()<=150 && EMAIL.matcher(email.trim()).matches(); }
  public static boolean validPassword(String password) { return password!=null && password.length()>=8 && password.length()<=72; }
  public static boolean validPrice(BigDecimal price) { return price!=null && price.signum()>=0 && price.scale()<=2 && price.precision()<=10; }
}
