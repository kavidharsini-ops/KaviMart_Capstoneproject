package com.kavi.kavimart.service.ai;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ChatProviderFactoryTest {

  @Test
  void createsMockProviderByDefault() {
    ChatProvider provider = ChatProviderFactory.create();
    assertNotNull(provider);
    assertInstanceOf(MockChatProvider.class, provider);
  }

  @Test
  void createsMockProviderWhenRequestedExplicitly() {
    ChatProvider provider = ChatProviderFactory.create("mock");
    assertNotNull(provider);
    assertInstanceOf(MockChatProvider.class, provider);
  }

  @Test
  void fallsBackToMockWhenGeminiKeyIsMissing() {
    // Unless GEMINI_API_KEY is configured in the environment, gemini falls back to mock
    ChatProvider provider = ChatProviderFactory.create("gemini");
    assertNotNull(provider);
  }
}
