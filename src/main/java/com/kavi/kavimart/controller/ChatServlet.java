package com.kavi.kavimart.controller;

import com.google.gson.JsonParseException;
import com.kavi.kavimart.dto.ChatRequestDTO;
import com.kavi.kavimart.dto.ChatResponseDTO;
import com.kavi.kavimart.exception.ValidationException;
import com.kavi.kavimart.service.ai.ChatProviderFactory;
import com.kavi.kavimart.service.ai.ChatService;
import com.kavi.kavimart.service.ai.ChatSessionState;
import com.kavi.kavimart.util.JsonUtil;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** POST /api/v1/chat: validates the request and hands the question to the chat service. */
@WebServlet(name = "chat", urlPatterns = "/api/v1/chat")
public class ChatServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;
  private static final String STATE_KEY = "kavimart.chat";
  private static final int MAX_BODY_BYTES = 4096;

  private transient ChatService chatService;

  /** Builds the chat service with the configured provider. */
  @Override
  public void init() {
    chatService = new ChatService(ChatProviderFactory.create());
  }

  /** Answers one chat question as JSON. */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    try {
      if (req.getContentLengthLong() > MAX_BODY_BYTES) {
        throw new ValidationException(Map.of("message", "That message is too long."));
      }
      ChatRequestDTO body;
      try {
        body = JsonUtil.gson().fromJson(req.getReader(), ChatRequestDTO.class);
      } catch (JsonParseException e) {
        throw new ValidationException(Map.of("message",
            "Send your question as JSON, for example {\"message\":\"hello\"}."));
      }
      String message = body == null ? null : body.getMessage();
      String reply = chatService.reply(sessionState(req), message);
      JsonUtil.success(res, 200, new ChatResponseDTO(reply));
    } catch (Throwable t) {
      error(req, res, t);
    }
  }

  private ChatSessionState sessionState(HttpServletRequest req) {
    HttpSession session = req.getSession(true);
    synchronized (this) {
      Object value = session.getAttribute(STATE_KEY);
      if (value instanceof ChatSessionState existing) {
        return existing;
      }
      ChatSessionState created = new ChatSessionState();
      session.setAttribute(STATE_KEY, created);
      return created;
    }
  }
}