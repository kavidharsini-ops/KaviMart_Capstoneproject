package com.kavi.kavimart.dto;

/** JSON reply returned to the chat widget. */
public class ChatResponseDTO {
  private final String reply;

  /**
   * Creates a reply.
   *
   * @param reply the text to show
   */
  public ChatResponseDTO(String reply) {
    this.reply = reply;
  }

  /**
   * Returns the reply text.
   *
   * @return the reply
   */
  public String getReply() {
    return reply;
  }
}