package com.sentinel.backend.security;

import com.sentinel.backend.auth.AuditEventRepository;
import com.sentinel.backend.auth.RefreshTokenRepository;
import com.sentinel.backend.user.Role;
import com.sentinel.backend.user.User;
import com.sentinel.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired RefreshTokenRepository refreshTokens;
    @Autowired AuditEventRepository audits;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtService jwt;

    @BeforeEach
    void clean() {
        refreshTokens.deleteAll();
        audits.deleteAll();
        users.deleteAll();
    }

    @Test
    void publicHealthIsAccessible() throws Exception {
        mvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void protectedEndpointRejectsAnonymousRequest() throws Exception {
        mvc.perform(get("/api/v1/test/viewer"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void registerAndLoginIssueTokens() throws Exception {
        String register = "{\"email\":\"analyst@example.com\",\"password\":\"StrongPass123!\"}";
        mvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(register))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("analyst@example.com"))
                .andExpect(jsonPath("$.role").value("VIEWER"));

        String login = "{\"email\":\"analyst@example.com\",\"password\":\"StrongPass123!\"}";
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void viewerCannotAccessAdminEndpoint() throws Exception {
        User user = saveUser("viewer@example.com", Role.VIEWER);
        String token = jwt.generateAccessToken(user);

        mvc.perform(get("/api/v1/test/admin").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void adminCanAccessAdminEndpoint() throws Exception {
        User user = saveUser("admin@example.com", Role.ADMIN);
        String token = jwt.generateAccessToken(user);

        mvc.perform(get("/api/v1/test/admin").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("ADMIN access granted"));
    }

    private User saveUser(String email, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(encoder.encode("StrongPass123!"));
        user.setRole(role);
        user.setEnabled(true);
        return users.save(user);
    }
}
