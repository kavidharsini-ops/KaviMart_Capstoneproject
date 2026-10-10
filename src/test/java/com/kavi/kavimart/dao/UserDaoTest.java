package com.kavi.kavimart.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kavi.kavimart.dao.jdbc.JdbcUserDao;
import com.kavi.kavimart.exception.ConflictException;
import com.kavi.kavimart.model.User;
import org.junit.jupiter.api.Test;

class UserDaoTest {

  @Test
  void findsSeededUsersAndCreatesAccounts() {
    JdbcUserDao dao = new JdbcUserDao(TestDatabase.create("userdao"));
    assertEquals("ADMIN", dao.findByEmail("admin@kavimart.com").orElseThrow().getRole());
    User user = new User(0, "Test Buyer", "newbuyer@example.com", "$2a$10$sample", "BUYER", true);
    User saved = dao.create(user);
    assertEquals(7, saved.getId());
    assertEquals("newbuyer@example.com", dao.findById(saved.getId()).orElseThrow().getEmail());

    User user2 = new User(0, "Test Buyer 2", "newbuyer2@example.com", "$2a$10$sample", "BUYER", true);
    User saved2 = dao.create(user2);
    assertEquals(8, saved2.getId());

    // Duplicate email check
    assertThrows(ConflictException.class, () ->
        dao.create(new User(0, "Duplicate", "admin@kavimart.com", "$2a$10$sample", "BUYER", true)));
  }

  @Test
  void activeFlagCanBeChanged() {
    JdbcUserDao dao = new JdbcUserDao(TestDatabase.create("useractive"));
    assertTrue(dao.setActive(4, false));
    assertFalse(dao.findById(4).orElseThrow().isActive());
    assertFalse(dao.setActive(9999, false));
  }
}
