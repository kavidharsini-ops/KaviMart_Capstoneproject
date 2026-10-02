package com.kavi.kavimart.model;

/** Authenticated account entity. */
public class User {
 private long id; private String name,email,passwordHash,role; private boolean active=true;
 public User(){}
 public User(long id,String name,String email,String passwordHash,String role,boolean active){this.id=id;this.name=name;this.email=email;this.passwordHash=passwordHash;this.role=role;this.active=active;}
 public long getId(){return id;} public void setId(long v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;} public String getRole(){return role;} public void setRole(String v){role=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
