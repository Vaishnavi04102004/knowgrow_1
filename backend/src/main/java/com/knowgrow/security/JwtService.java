package com.knowgrow.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    public static final String COOKIE = "kg_token";
    private final SecretKey key;
    private final Duration ttl;
    private final boolean secureCookie;

    public JwtService(@Value("${knowgrow.jwt.secret}") String secret,
                      @Value("${knowgrow.jwt.ttl-hours}") long hours,
                      @Value("${knowgrow.jwt.cookie-secure}") boolean secureCookie) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttl = Duration.ofHours(hours);
        this.secureCookie = secureCookie;
    }

    public String issue(String userId, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder().subject(userId).claim("role", role)
            .issuedAt(new Date(now)).expiration(new Date(now + ttl.toMillis()))
            .signWith(key).compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public ResponseCookie cookie(String token) {
        return ResponseCookie.from(COOKIE, token).httpOnly(true).secure(secureCookie)
            .sameSite("Lax").path("/").maxAge(ttl).build();
    }

    public ResponseCookie clearCookie() {
        return ResponseCookie.from(COOKIE, "").httpOnly(true).secure(secureCookie)
            .sameSite("Lax").path("/").maxAge(0).build();
    }
}
