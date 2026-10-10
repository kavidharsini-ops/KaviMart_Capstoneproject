package com.kavi.kavimart.dao;

import com.kavi.kavimart.model.PageResult;
import com.kavi.kavimart.model.Product;
import com.kavi.kavimart.model.ProductQuery;
import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for catalog and seller product listings.
 */
public interface ProductDao {

  /**
   * Searches active product listings matching the specified query criteria with pagination.
   *
   * @param query criteria object containing search keywords, category filter, sorting, and page offsets
   * @return paginated search results
   */
  PageResult<Product> search(ProductQuery query);

  /**
   * Finds an active product by its primary key.
   *
   * @param id the product ID
   * @return optional containing the product if found and active
   */
  Optional<Product> findById(long id);

  /**
   * Retrieves all active listings owned by a specific seller.
   *
   * @param sellerId the ID of the seller
   * @return list of active products belonging to the seller
   */
  List<Product> findBySeller(long sellerId);

  /**
   * Inserts a new product listing into the database.
   *
   * @param product the product entity to create
   * @return the created product with its generated ID
   */
  Product create(Product product);

  /**
   * Updates an existing listing ensuring it is owned by the specified seller.
   *
   * @param product the product entity containing updated attributes
   * @param sellerId the seller ID asserting ownership
   * @return true if updated successfully, false otherwise
   */
  boolean updateOwned(Product product, long sellerId);

  /**
   * Soft-deletes a listing owned by the seller by marking is_active = FALSE.
   *
   * @param productId the product ID to hide
   * @param sellerId the seller ID asserting ownership
   * @return true if deactivated, false otherwise
   */
  boolean deleteOwned(long productId, long sellerId);

  /**
   * Soft-deletes any listing for administrative moderation.
   *
   * @param productId the product ID to hide
   * @return true if deactivated, false otherwise
   */
  boolean deleteAny(long productId);

  /**
   * Retrieves all active product listings for administrative review.
   *
   * @return list of all active products
   */
  List<Product> findAll();
}
