package com.kavi.kavimart.dao;

import com.kavi.kavimart.dao.jdbc.JdbcProductDao;import com.kavi.kavimart.model.ProductQuery;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class ProductDaoTest {
 @Test void filtersAndPaginatesCatalog(){JdbcProductDao dao=new JdbcProductDao(TestDatabase.create("productsearch"));var page=dao.search(new ProductQuery("handwoven","","price_asc",1,1));assertEquals(1,page.getTotal());assertEquals("Handwoven Indigo Tote",page.getItems().get(0).getName());assertEquals(1,page.getPages());}
 @Test void sellerOwnershipIsAppliedToEditsAndDeletes(){JdbcProductDao dao=new JdbcProductDao(TestDatabase.create("productowner"));var product=dao.findById(101).orElseThrow();product.setPrice(new java.math.BigDecimal("35.00"));assertFalse(dao.updateOwned(product,3));assertTrue(dao.updateOwned(product,2));assertFalse(dao.deleteOwned(101,3));assertTrue(dao.deleteOwned(101,2));}
}
