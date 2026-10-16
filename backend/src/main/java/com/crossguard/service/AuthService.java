package com.crossguard.service;

import com.crossguard.model.UserAccount;
import com.crossguard.repo.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
    public record UserView(String id, String email, String displayName) {}
    public record AuthView(String token, UserView user) {}
    public AuthView register(String name, String email, String password) {
        String cleanEmail=cleanEmail(email); String cleanName=name==null?"":name.trim();
        if(cleanName.length()<2 || cleanName.length()>80) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name must contain 2–80 characters.");
        if(password==null || password.length()<10 || password.length()>72) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password must be 10–72 characters.");
        if(users.existsByEmailIgnoreCase(cleanEmail)) throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists.");
        UserAccount user=users.save(new UserAccount(cleanEmail,cleanName,encoder.encode(password)));
        return new AuthView(jwt.issue(user),view(user));
    }
    public AuthView login(String email, String password) {
        UserAccount user=users.findByEmailIgnoreCase(cleanEmail(email)).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect."));
        if(password==null || !encoder.matches(password,user.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Email or password is incorrect.");
        return new AuthView(jwt.issue(user),view(user));
    }
    public UserAccount requireUser(String authorization) {
        if(authorization==null || !authorization.startsWith("Bearer ")) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Sign in to use Actual Mode.");
        try {
            UserAccount user=users.findById(jwt.userId(authorization.substring(7).trim())).orElse(null);
            if(user==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account no longer exists.");
            return user;
        } catch(JwtException | IllegalArgumentException ex) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Session expired. Sign in again."); }
    }
    public UserView view(UserAccount user){return new UserView(user.getId().toString(),user.getEmail(),user.getDisplayName());}
    private String cleanEmail(String email){
        String value=email==null?"":email.trim().toLowerCase(Locale.ROOT);
        if(value.length()>254 || !value.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Enter a valid email address.");
        return value;
    }
}
