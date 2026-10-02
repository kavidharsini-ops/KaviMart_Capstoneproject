package com.kavi.kavimart.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

class ChatServletTest {

  private String post(String json, StringWriter out, HttpServletResponse res) throws Exception {
    ChatServlet servlet = new ChatServlet();
    servlet.init();
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpSession session = mock(HttpSession.class);
    when(req.getContentLengthLong()).thenReturn(-1L);
    when(req.getRequestURI()).thenReturn("/api/v1/chat");
    when(req.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
    when(req.getSession(true)).thenReturn(session);
    when(res.getWriter()).thenReturn(new PrintWriter(out));
    servlet.doPost(req, res);
    return out.toString();
  }

  @Test
  void validQuestionReturnsSuccessEnvelopeWithReply() throws Exception {
    HttpServletResponse res = mock(HttpServletResponse.class);
    String body = post("{\"message\":\"how do I cancel an order?\"}", new StringWriter(), res);
    verify(res).setStatus(200);
    assertTrue(body.contains("\"success\":true"));
    assertTrue(body.contains("\"reply\""));
  }

  @Test
  void blankQuestionReturns400WithValidationError() throws Exception {
    HttpServletResponse res = mock(HttpServletResponse.class);
    String body = post("{\"message\":\"   \"}", new StringWriter(), res);
    verify(res).setStatus(400);
    assertTrue(body.contains("VALIDATION_ERROR"));
  }
}