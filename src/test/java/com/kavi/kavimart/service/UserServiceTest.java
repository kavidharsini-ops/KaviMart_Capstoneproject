package com.kavi.kavimart.service;

import com.kavi.kavimart.dao.UserDao;import com.kavi.kavimart.exception.*;import com.kavi.kavimart.model.User;import com.kavi.kavimart.util.PasswordUtil;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.extension.ExtendWith;import org.mockito.InjectMocks;import org.mockito.Mock;import org.mockito.junit.jupiter.MockitoExtension;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class) class UserServiceTest {
 @Mock UserDao dao;@InjectMocks UserService service;
 @Test void registrationHashesPasswordAndReturnsSafeDto(){when(dao.create(any())).thenAnswer(i->i.getArgument(0));var result=service.register("Jamie","jamie@example.com","secretPass1","BUYER");assertEquals("jamie@example.com",result.getEmail());assertFalse(result.toString().contains("secretPass1"));verify(dao).create(argThat(u->PasswordUtil.matches("secretPass1",u.getPasswordHash())));}
 @Test void invalidRegistrationStopsBeforeDao(){assertThrows(ValidationException.class,()->service.register("","bad","short","ADMIN"));verifyNoInteractions(dao);}
 @Test void bannedAccountsCannotLogin(){User banned=new User(1,"B","banned@example.com",PasswordUtil.hash("secretPass1"),"BUYER",false);when(dao.findByEmail("banned@example.com")).thenReturn(java.util.Optional.of(banned));AppException error=assertThrows(AppException.class,()->service.login("banned@example.com","secretPass1"));assertEquals(401,error.getStatus());}
}
