package com.kavi.kavimart.service;

import com.kavi.kavimart.dao.ProductDao;import com.kavi.kavimart.exception.ValidationException;import com.kavi.kavimart.model.Product;import java.math.BigDecimal;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.InjectMocks;import org.mockito.Mock;import org.mockito.junit.jupiter.MockitoExtension;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class) class ProductServiceTest {
 @Mock ProductDao dao;@InjectMocks ProductService service;
 @Test void invalidProductIsRejectedBeforePersistence(){Product p=new Product();p.setName("");assertThrows(ValidationException.class,()->service.create(2,p));verifyNoInteractions(dao);}
 @Test void sellerUpdateIncludesOwnerInPersistenceCheck(){Product p=new Product(101,2,"Maker","Item","Description",new BigDecimal("1.00"),2,"Home","",null);when(dao.updateOwned(p,2)).thenReturn(true);service.update(2,p);verify(dao).updateOwned(p,2);}
}
