package com.kavi.kavimart.service.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.kavi.kavimart.exception.RateLimitException;
import com.kavi.kavimart.exception.ValidationException;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class ChatServiceTest {
  private final ChatProvider provider = mock(ChatProvider.class);
  private final AtomicLong clock = new AtomicLong(1_000L);
  private final ChatService service = new ChatService(provider, clock::get);
  private final ChatSessionState state = new ChatSessionState();

  @Test
  void returnsProviderReplyForCleanedQuestion() {
    when(provider.getReply(anyString(), anyString())).thenReturn("hello there");
    assertEquals("hello there", service.reply(state, "  Hi   there "));
    verify(provider).getReply(eq("Hi there"), anyString());
  }

  @Test
  void rejectsBlankMessage() {
    assertThrows(ValidationException.class, () -> service.reply(state, "   "));
    verifyNoInteractions(provider);
  }

  @Test
  void rejectsNullMessage() {
    assertThrows(ValidationException.class, () -> service.reply(state, null));
  }

  @Test
  void rejectsMessageOverTheLengthCap() {
    String tooLong = "a".repeat(ChatService.MAX_MESSAGE_LENGTH + 1);
    assertThrows(ValidationException.class, () -> service.reply(state, tooLong));
    verifyNoInteractions(provider);
  }

  @Test
  void acceptsMessageAtTheLengthCap() {
    when(provider.getReply(anyString(), anyString())).thenReturn("ok");
    String exact = "a".repeat(ChatService.MAX_MESSAGE_LENGTH);
    assertEquals("ok", service.reply(state, exact));
  }

  @Test
  void eleventhMessageInOneMinuteIsRejected() {
    when(provider.getReply(anyString(), anyString())).thenReturn("ok");
    for (int i = 1; i <= ChatService.RATE_LIMIT; i++) {
      service.reply(state, "question " + i);
    }
    assertThrows(RateLimitException.class, () -> service.reply(state, "question 11"));
  }

  @Test
  void allowsMessagesAgainAfterTheWindow() {
    when(provider.getReply(anyString(), anyString())).thenReturn("ok");
    for (int i = 1; i <= ChatService.RATE_LIMIT; i++) {
      service.reply(state, "question " + i);
    }
    clock.set(1_000L + ChatService.WINDOW_MILLIS);
    assertEquals("ok", service.reply(state, "another question"));
  }

  @Test
  void repeatedQuestionIsServedFromTheSessionCache() {
    when(provider.getReply(anyString(), anyString())).thenReturn("ok");
    service.reply(state, "How do I pay?");
    service.reply(state, "  how do i PAY? ");
    verify(provider, times(1)).getReply(anyString(), anyString());
  }

  @Test
  void returnsFallbackWhenTheProviderFails() {
    when(provider.getReply(anyString(), anyString())).thenThrow(new IllegalStateException("boom"));
    assertEquals(ChatService.FALLBACK_REPLY, service.reply(state, "hello"));
  }

  @Test
  void returnsFallbackWhenTheProviderReturnsNothing() {
    when(provider.getReply(anyString(), anyString())).thenReturn("  ");
    assertEquals(ChatService.FALLBACK_REPLY, service.reply(state, "hello"));
  }
}