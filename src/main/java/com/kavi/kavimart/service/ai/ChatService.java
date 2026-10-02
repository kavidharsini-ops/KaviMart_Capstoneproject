package com.kavi.kavimart.service.ai;

import com.kavi.kavimart.exception.RateLimitException;
import com.kavi.kavimart.exception.ValidationException;
import java.util.Locale;
import java.util.Map;
import java.util.function.LongSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Validates chat questions, enforces the rate limit, caches answers and calls the provider. */
public class ChatService {
  /** Longest question accepted, in characters. */
  public static final int MAX_MESSAGE_LENGTH = 300;
  /** Messages allowed per session in one window. */
  public static final int RATE_LIMIT = 10;
  /** Rate-limit window length in milliseconds (one minute). */
  public static final long WINDOW_MILLIS = 60_000L;
  /** Reply shown when the provider fails. */
  public static final String FALLBACK_REPLY =
      "Sorry, the helper is resting right now. Please try again in a little while, "
          + "or browse the Shop page.";

  private static final String SHOP_CONTEXT =
      "KaviMart is a multi-seller online marketplace where sellers list products and "
          + "buyers browse, add to bag, check out and review.";
  private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

  private final ChatProvider provider;
  private final LongSupplier clock;

  /**
   * Creates a service that uses the real clock.
   *
   * @param provider the reply source
   */
  public ChatService(ChatProvider provider) {
    this(provider, System::currentTimeMillis);
  }

  /**
   * Creates a service with a custom clock (used by tests).
   *
   * @param provider the reply source
   * @param clock supplies the current time in milliseconds
   */
  public ChatService(ChatProvider provider, LongSupplier clock) {
    this.provider = provider;
    this.clock = clock;
  }

  /**
   * Answers one question for a session.
   *
   * @param state the session's chat memory
   * @param rawMessage the question as typed
   * @return the reply text, or a friendly fallback if the provider fails
   */
  public String reply(ChatSessionState state, String rawMessage) {
    String message = clean(rawMessage);
    if (message.isEmpty()) {
      throw new ValidationException(Map.of("message", "Please type a question."));
    }
    if (message.length() > MAX_MESSAGE_LENGTH) {
      throw new ValidationException(Map.of("message",
          "Please keep your question under " + MAX_MESSAGE_LENGTH + " characters."));
    }
    if (!state.tryAcquire(clock.getAsLong(), RATE_LIMIT, WINDOW_MILLIS)) {
      throw new RateLimitException();
    }
    String key = message.toLowerCase(Locale.ROOT);
    String cached = state.cached(key);
    if (cached != null) {
      return cached;
    }
    String reply;
    try {
      reply = provider.getReply(message, SHOP_CONTEXT);
    } catch (RuntimeException e) {
      LOG.warn("Chat provider failed: {}", e.getClass().getSimpleName());
      return FALLBACK_REPLY;
    }
    if (reply == null || reply.isBlank()) {
      return FALLBACK_REPLY;
    }
    state.remember(key, reply);
    return reply;
  }

  private static String clean(String raw) {
    if (raw == null) {
      return "";
    }
    return raw.replaceAll("\\p{Cntrl}", " ").trim().replaceAll("\\s+", " ");
  }
}