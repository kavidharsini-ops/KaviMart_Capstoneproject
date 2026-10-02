package com.kavi.kavimart.dto;
/** Safe user view. Deliberately excludes the password hash. */
public final class UserResponseDTO {
  private final long id; private final String name,email,role; private final boolean active;
  public UserResponseDTO(long id,String name,String email,String role,boolean active) { this.id=id;this.name=name;this.email=email;this.role=role;this.active=active; }
  public long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getRole(){return role;} public boolean isActive(){return active;}
}
