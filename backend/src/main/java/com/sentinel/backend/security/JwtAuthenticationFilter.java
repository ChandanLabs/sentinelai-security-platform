package com.sentinel.backend.security;

import com.sentinel.backend.user.User;
import com.sentinel.backend.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt; private final UserRepository users;
    public JwtAuthenticationFilter(JwtService jwt,UserRepository users){this.jwt=jwt;this.users=users;}
    @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
        String auth=req.getHeader("Authorization");
        if(auth!=null && auth.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication()==null){
            String token=auth.substring(7);
            try {
                String email=jwt.extractSubject(token); User user=users.findByEmailIgnoreCase(email).orElse(null);
                if(user!=null && jwt.isValid(token,user)){
                    var authorities=java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_"+user.getRole().name()));
                    var authentication=new UsernamePasswordAuthenticationToken(user.getEmail(),null,authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch(RuntimeException ignored) { /* Invalid bearer tokens remain unauthenticated. */ }
        }
        chain.doFilter(req,res);
    }
}
