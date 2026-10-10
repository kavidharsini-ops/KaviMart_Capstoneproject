package com.kavi.kavimart.filter;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Adds a per-request correlation ID to logs and the response headers.
 */
public class LoggingFilter implements Filter {
  private static final Logger LOG = LoggerFactory.getLogger(LoggingFilter.class);

  /** {@inheritDoc} */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String id = UUID.randomUUID().toString();
    MDC.put("requestId", id);
    if (response instanceof HttpServletResponse http) {
      http.setHeader("X-Request-ID", id);
    }
    long start = System.nanoTime();
    try {
      chain.doFilter(request, response);
    } finally {
      long duration = (System.nanoTime() - start) / 1_000_000;
      String method = request instanceof HttpServletRequest h ? h.getMethod() : "";
      String uri = request instanceof HttpServletRequest h ? h.getRequestURI() : "";
      LOG.info("{} {} completed in {}ms", method, uri, duration);
      MDC.remove("requestId");
    }
  }
}
