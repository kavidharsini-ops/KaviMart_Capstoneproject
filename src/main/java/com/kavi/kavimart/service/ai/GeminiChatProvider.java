package com.kavi.kavimart.service.ai;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chatbot provider that calls the Google Gemini API over HTTP.
 * Keeps the API key server-side and applies an 8-second request timeout.
 */
public class GeminiChatProvider implements ChatProvider {
  private static final Logger LOG = LoggerFactory.getLogger(GeminiChatProvider.class);
  private static final String DEFAULT_ENDPOINT =
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
  private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(8);
  private static final String PROMPT_TEMPLATE =
      "You are KaviMart's AI assistant. KaviMart is a multi-seller online marketplace.\n"
          + "Context: %s\n\n"
          + "Rules:\n"
          + "- Only answer questions about KaviMart products, orders, shopping bag, checkout, "
          + "cancellations, returns, reviews, and selling.\n"
          + "- If asked about anything outside KaviMart, politely decline and steer the user "
          + "back to KaviMart.\n"
          + "- Keep your responses concise, helpful, and polite.\n\n"
          + "User question: %s";

  private final String apiKey;
  private final HttpClient httpClient;
  private final Duration timeout;
  private final String endpoint;
  private final Gson gson = new Gson();

  /**
   * Constructs the provider using the GEMINI_API_KEY environment variable.
   */
  public GeminiChatProvider() {
    this(System.getenv("GEMINI_API_KEY"));
  }

  /**
   * Constructs the provider with a specific API key.
   *
   * @param apiKey the Gemini API key
   */
  public GeminiChatProvider(String apiKey) {
    this(apiKey, HttpClient.newBuilder().connectTimeout(DEFAULT_TIMEOUT).build(),
        DEFAULT_TIMEOUT, DEFAULT_ENDPOINT);
  }

  /**
   * Constructs the provider with a custom HTTP client (useful for testing).
   *
   * @param apiKey the Gemini API key
   * @param httpClient custom HTTP client
   */
  public GeminiChatProvider(String apiKey, HttpClient httpClient) {
    this(apiKey, httpClient, DEFAULT_TIMEOUT, DEFAULT_ENDPOINT);
  }

  /**
   * Constructs the provider with full configuration.
   *
   * @param apiKey the Gemini API key
   * @param httpClient custom HTTP client
   * @param timeout request timeout
   * @param endpoint Gemini API endpoint URL
   */
  public GeminiChatProvider(String apiKey, HttpClient httpClient, Duration timeout,
      String endpoint) {
    this.apiKey = apiKey == null ? "" : apiKey.trim();
    this.httpClient = httpClient;
    this.timeout = timeout;
    this.endpoint = endpoint;
  }

  /**
   * Obtains a chatbot response from Gemini API, or returns a fallback reply on failure.
   *
   * @param userMessage the cleaned user question
   * @param context shop context background text
   * @return reply string
   */
  @Override
  public String getReply(String userMessage, String context) {
    if (apiKey.isEmpty()) {
      LOG.warn("Gemini API key is not configured.");
      return ChatService.FALLBACK_REPLY;
    }
    if (userMessage == null || userMessage.isBlank()) {
      return MockChatProvider.OUT_OF_SCOPE_REPLY;
    }

    try {
      String fullPrompt = String.format(PROMPT_TEMPLATE, context, userMessage.trim());
      JsonObject root = new JsonObject();
      JsonArray contents = new JsonArray();
      JsonObject content = new JsonObject();
      JsonArray parts = new JsonArray();
      JsonObject part = new JsonObject();
      part.addProperty("text", fullPrompt);
      parts.add(part);
      content.add("parts", parts);
      contents.add(content);
      root.add("contents", contents);

      String requestBody = gson.toJson(root);
      URI uri = URI.create(endpoint + "?key=" + apiKey);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(uri)
          .timeout(timeout)
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(requestBody))
          .build();

      HttpResponse<String> response = httpClient.send(request,
          HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        LOG.warn("Gemini API responded with HTTP status {}", response.statusCode());
        return ChatService.FALLBACK_REPLY;
      }

      JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
      JsonArray candidates = responseJson.getAsJsonArray("candidates");
      if (candidates != null && !candidates.isEmpty()) {
        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
        JsonObject candContent = firstCandidate.getAsJsonObject("content");
        if (candContent != null) {
          JsonArray candParts = candContent.getAsJsonArray("parts");
          if (candParts != null && !candParts.isEmpty()) {
            String replyText = candParts.get(0).getAsJsonObject().get("text").getAsString();
            if (replyText != null && !replyText.isBlank()) {
              return replyText.trim();
            }
          }
        }
      }
      return ChatService.FALLBACK_REPLY;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOG.warn("Gemini request interrupted", e);
      return ChatService.FALLBACK_REPLY;
    } catch (Exception e) {
      LOG.warn("Error calling Gemini API: {}", e.getMessage());
      return ChatService.FALLBACK_REPLY;
    }
  }
}
