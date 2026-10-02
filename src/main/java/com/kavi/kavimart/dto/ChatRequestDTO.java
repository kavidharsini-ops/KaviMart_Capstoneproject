package com.kavi.kavimart.dto;

/** JSON body sent by the chat widget. */
public class ChatRequestDTO {
  private String message;

  /**
   * Returns the question typed by the visitor.
   *
   * @return the message text
   */
  public String getMessage() {
    return message;
  }
}