package com.kavi.kavimart.service.ai;

/** Source of chatbot replies; implementations can be swapped through configuration. */
public interface ChatProvider {

  /**
   * Returns a reply for the visitor's question.
   *
   * @param userMessage the cleaned question typed by the visitor
   * @param context fixed, server-side background text about the shop
   * @return the reply text
   */
  String getReply(String userMessage, String context);
}