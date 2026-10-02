package com.kavi.kavimart.dao;

import com.kavi.kavimart.dao.jdbc.JdbcUserDao;import com.kavi.kavimart.model.User;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class UserDaoTest {
 @Test void findsSeededUsersAndCreatesAccounts(){JdbcUserDao dao=new JdbcUserDao(TestDatabase.create("userdao"));assertEquals("ADMIN",dao.findByEmail("admin@kavimart.com").orElseThrow().getRole());User user=new User(0,"Test Buyer","newbuyer@example.com","$2a$10$sample","BUYER",true);User saved=dao.create(user);assertTrue(saved.getId()>0);assertEquals("newbuyer@example.com",dao.findById(saved.getId()).orElseThrow().getEmail());}
 @Test void activeFlagCanBeChanged(){JdbcUserDao dao=new JdbcUserDao(TestDatabase.create("useractive"));assertTrue(dao.setActive(4,false));assertFalse(dao.findById(4).orElseThrow().isActive());assertFalse(dao.setActive(9999,false));}
}
