package com.kavi.kavimart.service.ai;

import java.util.List;
import java.util.Locale;

/** Offline provider that answers common KaviMart questions from a fixed FAQ list. */
public class MockChatProvider implements ChatProvider {

  /** Reply used when a question is not about shopping on KaviMart. */
  public static final String OUT_OF_SCOPE_REPLY =
      "I can help with questions about browsing, buying and selling on KaviMart. "
          + "Try asking about orders, your bag, reviews or how to sell.";

  private record Faq(List<String> keywords, String answer) { }

  private static final List<Faq> FAQS = List.of(
      new Faq(List.of("hello", "hi ", "hey", "good morning", "good evening"),
          "Hello! I'm the KaviMart helper. Ask me about orders, your bag, reviews, "
              + "searching for products or selling on KaviMart."),
      new Faq(List.of("thank"),
          "You're welcome! Let me know if there is anything else I can help with."),
      new Faq(List.of("cancel"),
          "You can cancel an order while it is still Pending. Open My orders and choose "
              + "Cancel on that order. The items go back into stock."),
      new Faq(List.of("status", "track", "where is my order", "ship", "deliver"),
          "Every order moves through Pending, Confirmed, Shipped and Delivered. Open My "
              + "orders to see the current status. The seller updates it as your order "
              + "progresses."),
      new Faq(List.of("pay", "card ", "credit", "upi", "checkout"),
          "Checkout uses a demo payment confirmation, so no real money is taken. Add items "
              + "to your bag, open Bag, then check out."),
      new Faq(List.of("buy", "purchase", "place an order", "place order", "how to order"),
          "Choose Add to bag on a product, open Bag to review your items, then check out. "
              + "You will find the order under My orders."),
      new Faq(List.of("bag", "cart"),
          "Use Add to bag on any product. In Bag you can change quantities, remove items "
              + "and see your running total. You can only add up to the stock available."),
      new Faq(List.of("search", "find", "filter", "categor", "sort"),
          "Use the search box on the Shop page to look up products by keyword. You can "
              + "also filter by category and sort the results."),
      new Faq(List.of("sell", "listing", "add product"),
          "To sell, register with the Seller role. Then open My listings to add a product "
              + "with its name, description, price, stock, category and image link. You can "
              + "edit or remove it later and follow incoming orders under Orders."),
      new Faq(List.of("review", "rating", "stars", "star "),
          "Once an order shows Delivered you can open the product page and leave a 1 to 5 "
              + "star rating with a comment. Each product can be reviewed once."),
      new Faq(List.of("register", "sign up", "join", "login", "log in", "sign in", "account"),
          "Choose Join us to create a Buyer or Seller account, or Sign in if you already "
              + "have one."),
      new Faq(List.of("price", "cost", "currency", "rupee", "inr"),
          "All prices on KaviMart are shown in Indian rupees (\u20B9)."),
      new Faq(List.of("stock", "available", "sold out"),
          "Each product has a stock count set by the seller. You can't add more than what "
              + "is in stock."));

  /** {@inheritDoc} */
  @Override
  public String getReply(String userMessage, String context) {
    String lower = userMessage == null ? "" : userMessage.toLowerCase(Locale.ROOT);
    String text = " " + lower.replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim() + " ";
    for (Faq faq : FAQS) {
      for (String keyword : faq.keywords()) {
        if (text.contains(" " + keyword)) {
          return faq.answer();
        }
      }
    }
    return OUT_OF_SCOPE_REPLY;
  }
}