package com.kavi.kavimart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.kavi.kavimart.dao.ProductDao;
import com.kavi.kavimart.dao.ReviewDao;
import com.kavi.kavimart.exception.AppException;
import com.kavi.kavimart.exception.ResourceNotFoundException;
import com.kavi.kavimart.exception.ValidationException;
import com.kavi.kavimart.model.Product;
import com.kavi.kavimart.model.Review;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock
  ReviewDao reviewDao;

  @Mock
  ProductDao productDao;

  @InjectMocks
  ReviewService reviewService;

  @Test
  void invalidRatingThrowsValidationException() {
    assertThrows(ValidationException.class, () ->
        reviewService.create(1L, 101L, 6, "Great item!"));
    assertThrows(ValidationException.class, () ->
        reviewService.create(1L, 101L, 0, "Too low rating"));
    verifyNoInteractions(productDao, reviewDao);
  }

  @Test
  void nonExistentProductThrowsResourceNotFoundException() {
    when(productDao.findById(999L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () ->
        reviewService.create(1L, 999L, 5, "Nice product!"));
  }

  @Test
  void unverifiedPurchaseThrowsForbiddenAppException() {
    Product product = new Product(101L, 2L, "Seller", "Kurti", "Desc",
        new BigDecimal("500.00"), 10, "Apparel", null, LocalDateTime.now());
    when(productDao.findById(101L)).thenReturn(Optional.of(product));
    when(reviewDao.hasDeliveredPurchase(1L, 101L)).thenReturn(false);

    AppException ex = assertThrows(AppException.class, () ->
        reviewService.create(1L, 101L, 5, "I never bought this"));
    assertEquals(403, ex.getStatus());
  }

  @Test
  void verifiedBuyerCreatesReviewSuccessfully() {
    Product product = new Product(101L, 2L, "Seller", "Kurti", "Desc",
        new BigDecimal("500.00"), 10, "Apparel", null, LocalDateTime.now());
    Review created = new Review(1L, 101L, 1L, "Buyer", 5, "Awesome quality", LocalDateTime.now());

    when(productDao.findById(101L)).thenReturn(Optional.of(product));
    when(reviewDao.hasDeliveredPurchase(1L, 101L)).thenReturn(true);
    when(reviewDao.create(1L, 101L, 5, "Awesome quality")).thenReturn(created);

    Review result = reviewService.create(1L, 101L, 5, "Awesome quality");
    assertEquals(5, result.getRating());
    assertEquals("Awesome quality", result.getComment());
  }

  @Test
  void listsReviewsAndCalculatesAverage() {
    Review r = new Review(1L, 101L, 1L, "Buyer", 4, "Good", LocalDateTime.now());
    when(reviewDao.findByProduct(101L)).thenReturn(List.of(r));
    when(reviewDao.averageRating(101L)).thenReturn(4.0);

    List<Review> list = reviewService.list(101L);
    double avg = reviewService.average(101L);

    assertEquals(1, list.size());
    assertEquals(4.0, avg);
    verify(reviewDao).findByProduct(101L);
    verify(reviewDao).averageRating(101L);
  }
}
