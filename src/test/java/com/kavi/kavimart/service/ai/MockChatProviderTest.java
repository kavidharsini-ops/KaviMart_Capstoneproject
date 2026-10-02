package com.kavi.kavimart.service.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MockChatProviderTest {
  private final MockChatProvider provider = new MockChatProvider();

  @Test
  void answersCancelQuestion() {
    assertTrue(provider.getReply("How do I cancel my order?", "").contains("Pending"));
  }

  @Test
  void answersOrderStatusQuestion() {
    String reply = provider.getReply("Where is my order?", "");
    assertTrue(reply.contains("Pending, Confirmed, Shipped and Delivered"));
  }

  @Test
  void answersPaymentQuestion() {
    assertTrue(provider.getReply("can I pay by upi", "").contains("demo payment"));
  }

  @Test
  void answersSellingQuestion() {
    assertTrue(provider.getReply("how to sell products", "").contains("Seller"));
  }

  @Test
  void answersReviewQuestion() {
    assertTrue(provider.getReply("I want to leave a review", "").contains("star"));
  }

  @Test
  void answersCurrencyQuestion() {
    assertTrue(provider.getReply("what currency are prices in", "").contains("rupees"));
  }

  @Test
  void answersGreeting() {
    assertTrue(provider.getReply("hi", "").startsWith("Hello"));
  }

  @Test
  void refusesQuestionsOutsideTheShop() {
    assertEquals(MockChatProvider.OUT_OF_SCOPE_REPLY,
        provider.getReply("when is earth day", ""));
  }

  @Test
  void handlesNullMessage() {
    assertEquals(MockChatProvider.OUT_OF_SCOPE_REPLY, provider.getReply(null, ""));
  }
}