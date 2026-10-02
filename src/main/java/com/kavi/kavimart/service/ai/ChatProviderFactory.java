package com.kavi.kavimart.service.ai;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Chooses the chatbot provider from the ai.chatbot.provider setting (mock or gemini). */
public final class ChatProviderFactory {
  private static final Logger LOG = LoggerFactory.getLogger(ChatProviderFactory.class);

  private ChatProviderFactory() { }

  /**
   * Creates the provider named in the AI_CHATBOT_PROVIDER variable or config.properties.
   *
   * @return the configured provider, or the mock provider by default
   */
  public static ChatProvider create() {
    return create(configuredName());
  }

  /**
   * Creates the provider with the given name.
   *
   * @param name provider name such as "mock" or "gemini"
   * @return the matching provider
   */
  public static ChatProvider create(String name) {
    if ("gemini".equalsIgnoreCase(name)) {
      LOG.warn("Gemini provider is not installed yet; using the mock provider.");
    }
    return new MockChatProvider();
  }

  private static String configuredName() {
    String fromEnv = System.getenv("AI_CHATBOT_PROVIDER");
    if (fromEnv != null && !fromEnv.isBlank()) {
      return fromEnv.trim();
    }
    Properties props = new Properties();
    try (InputStream in = ChatProviderFactory.class.getResourceAsStream("/config.properties")) {
      if (in != null) {
        props.load(in);
      }
    } catch (IOException e) {
      LOG.warn("Could not read config.properties; using the mock chatbot provider.");
    }
    return props.getProperty("ai.chatbot.provider", "mock").trim();
  }
}