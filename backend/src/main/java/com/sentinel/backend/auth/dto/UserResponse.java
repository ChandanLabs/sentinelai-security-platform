package com.sentinel.backend.auth.dto;

import com.sentinel.backend.user.User;
import com.sentinel.backend.user.Role;
import java.util.UUID;
public record UserResponse(UUID id,String email,Role role,boolean enabled){
    public static UserResponse from(User u){return new UserResponse(u.getId(),u.getEmail(),u.getRole(),u.isEnabled());}
}
