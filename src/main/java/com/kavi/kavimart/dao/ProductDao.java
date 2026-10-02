package com.kavi.kavimart.dao;

import com.kavi.kavimart.model.PageResult;
import com.kavi.kavimart.model.Product;
import com.kavi.kavimart.model.ProductQuery;
import java.util.List;
import java.util.Optional;
/** Persistence contract for catalog and seller product management. */
public interface ProductDao {
 /** Search and page public listings. */ PageResult<Product> search(ProductQuery query);
 /** Find a listing by its primary key. */ Optional<Product> findById(long id);
 /** Return products owned by a seller. */ List<Product> findBySeller(long sellerId);
 /** Create a listing. */ Product create(Product product);
 /** Update a listing only when owned by the supplied seller. */ boolean updateOwned(Product product,long sellerId);
 /** Delete a listing only when owned by the supplied seller. */ boolean deleteOwned(long productId,long sellerId);
 /** Remove any listing for administration. */ boolean deleteAny(long productId);
 /** Return every listing for administrative moderation. */ List<Product> findAll();
}
