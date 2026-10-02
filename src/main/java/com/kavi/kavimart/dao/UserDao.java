package com.kavi.kavimart.dao;

import com.kavi.kavimart.model.User;
import java.util.List;
import java.util.Optional;
/** Persistence contract for account operations. */
public interface UserDao {
 /** Find one account by normalized email. */ Optional<User> findByEmail(String email);
 /** Find one account by primary key. */ Optional<User> findById(long id);
 /** Create a new account and return it with its generated key. */ User create(User user);
 /** Return all accounts in stable creation order. */ List<User> findAll();
 /** Change account activity state; returns false when the account does not exist. */ boolean setActive(long id,boolean active);
 /** Count all accounts. */ long count();
}
