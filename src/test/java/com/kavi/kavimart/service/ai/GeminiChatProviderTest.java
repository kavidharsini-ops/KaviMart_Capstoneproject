package com.kavi.kavimart.service.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeminiChatProviderTest {

  @Mock
  private HttpClient httpClient;

  @Test
  void successfulReplyFromGemini() throws Exception {
    @SuppressWarnings("unchecked")
    HttpResponse<String> response = (HttpResponse<String>) mock(HttpResponse.class);
    when(response.statusCode()).thenReturn(200);
    when(response.body()).thenReturn(
        "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"We have cotton kurtis in stock.\"}]}}]}");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(response);

    GeminiChatProvider provider = new GeminiChatProvider("test-key", httpClient);
    String reply = provider.getReply("Do you sell kurtis?", "KaviMart is a marketplace");
    assertEquals("We have cotton kurtis in stock.", reply);
  }

  @Test
  void non200StatusCodeReturnsFallback() throws Exception {
    @SuppressWarnings("unchecked")
    HttpResponse<String> response = (HttpResponse<String>) mock(HttpResponse.class);
    when(response.statusCode()).thenReturn(500);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(response);

    GeminiChatProvider provider = new GeminiChatProvider("test-key", httpClient);
    String reply = provider.getReply("How to check out?", "context");
    assertEquals(ChatService.FALLBACK_REPLY, reply);
  }

  @Test
  void networkExceptionReturnsFallback() throws Exception {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("Connection timed out"));

    GeminiChatProvider provider = new GeminiChatProvider("test-key", httpClient);
    String reply = provider.getReply("Where is my order?", "context");
    assertEquals(ChatService.FALLBACK_REPLY, reply);
  }

  @Test
  void emptyApiKeyReturnsFallback() {
    GeminiChatProvider provider = new GeminiChatProvider("", httpClient);
    String reply = provider.getReply("Can I cancel?", "context");
    assertEquals(ChatService.FALLBACK_REPLY, reply);
  }

  @Test
  void nullOrBlankMessageReturnsOutOfScope() {
    GeminiChatProvider provider = new GeminiChatProvider("test-key", httpClient);
    assertEquals(MockChatProvider.OUT_OF_SCOPE_REPLY, provider.getReply(null, "context"));
    assertEquals(MockChatProvider.OUT_OF_SCOPE_REPLY, provider.getReply("   ", "context"));
  }

  @Test
  void customTimeoutAndEndpointConfiguration() {
    GeminiChatProvider provider = new GeminiChatProvider("test-key", httpClient,
        Duration.ofSeconds(5), "https://custom.endpoint.com");
    // Verify constructor does not fail with custom endpoint
    assertEquals(MockChatProvider.OUT_OF_SCOPE_REPLY, provider.getReply("", "context"));
  }
}
