package com.example.weather;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Very basic login: one hard-coded user, tokens kept in memory.
 * Replace with a real user store + JWT/Spring Security for anything serious.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private static final String USERNAME = "demo";
    private static final String PASSWORD = "demo123";

    // Valid tokens (cleared when the server restarts)
    static final Set<String> TOKENS = ConcurrentHashMap.newKeySet();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String user = body.get("username");
        String pass = body.get("password");

        if (USERNAME.equals(user) && PASSWORD.equals(pass)) {
            String token = UUID.randomUUID().toString();
            TOKENS.add(token);
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Wrong username or password"));
    }

    static boolean isValid(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        return TOKENS.contains(authHeader.substring(7));
    }
}
