package com.kavi.kavimart.service.ai;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;

/** Per-session chat memory: recent message times for rate limiting and a reply cache. */
public final class ChatSessionState implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final int MAX_CACHED = 50;

  private final ArrayDeque<Long> sentAt = new ArrayDeque<>();
  private final LinkedHashMap<String, String> cache = new LinkedHashMap<>();

  /**
   * Records a message if the session is still under its limit.
   *
   * @param now current time in milliseconds
   * @param limit maximum messages allowed in the window
   * @param windowMillis length of the window in milliseconds
   * @return true if the message is allowed, false if the limit is reached
   */
  public synchronized boolean tryAcquire(long now, int limit, long windowMillis) {
    while (!sentAt.isEmpty() && now - sentAt.peekFirst() >= windowMillis) {
      sentAt.pollFirst();
    }
    if (sentAt.size() >= limit) {
      return false;
    }
    sentAt.addLast(now);
    return true;
  }

  /**
   * Looks up a reply remembered for an identical question.
   *
   * @param key the normalised question
   * @return the cached reply, or null
   */
  public synchronized String cached(String key) {
    return cache.get(key);
  }

  /**
   * Remembers a reply for an identical future question.
   *
   * @param key the normalised question
   * @param reply the reply to remember
   */
  public synchronized void remember(String key, String reply) {
    if (cache.size() >= MAX_CACHED) {
      cache.remove(cache.keySet().iterator().next());
    }
    cache.put(key, reply);
  }
}