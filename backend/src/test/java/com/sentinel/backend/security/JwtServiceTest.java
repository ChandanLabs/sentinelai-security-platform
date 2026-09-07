package com.sentinel.backend.security;

import com.sentinel.backend.user.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
 @Test void generatesTokenAndValidatesUser(){
  String secret="test-secret-that-is-at-least-32-characters-long"; JwtService jwt=new JwtService(secret,900000);
  User user=new User(); user.setEmail("viewer@example.com"); user.setRole(Role.VIEWER); user.setEnabled(true);
  String token=jwt.generateAccessToken(user);
  assertEquals("viewer@example.com",jwt.extractSubject(token)); assertTrue(jwt.isValid(token,user));
 }
 @Test void rejectsWrongUser(){
  JwtService jwt=new JwtService("test-secret-that-is-at-least-32-characters-long",900000);
  User a=new User();a.setEmail("a@example.com");a.setRole(Role.VIEWER);a.setEnabled(true);
  User b=new User();b.setEmail("b@example.com");b.setRole(Role.VIEWER);b.setEnabled(true);
  assertFalse(jwt.isValid(jwt.generateAccessToken(a),b));
 }
}
