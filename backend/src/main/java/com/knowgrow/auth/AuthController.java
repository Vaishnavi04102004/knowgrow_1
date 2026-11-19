package com.knowgrow.auth;

import com.knowgrow.common.ApiException;
import com.knowgrow.security.JwtService;
import com.knowgrow.user.UserDto;
import com.knowgrow.user.UserRepo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    record LoginReq(@NotBlank @Email String email, @NotBlank String password) {}
    record RegisterReq(@NotBlank @Size(max = 120) String name, @NotBlank @Email String email,
                       @NotBlank @Size(min = 8, max = 100) String password) {}

    private final UserRepo users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepo users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginReq req) {
        var found = users.findLogin(req.email())
            .filter(l -> l.passwordHash() != null && encoder.matches(req.password(), l.passwordHash()))
            .filter(l -> "ACTIVE".equals(l.status()))
            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Incorrect email or password."));
        String token = jwt.issue(found.user().id(), found.user().role());
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwt.cookie(token).toString())
            .body(found.user());
    }

    /** Public registration always creates an Employee. Privileged roles are assigned by HR only. */
    @PostMapping("/register")
    public Map<String, String> register(@Valid @RequestBody RegisterReq req) {
        if (users.emailExists(req.email()))
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists.");
        users.create(req.name().trim(), req.email(), encoder.encode(req.password()),
            "Employee", "General", "Unassigned", "Employee", UserRepo.initials(req.name()));
        return Map.of("message", "Account created. You can now sign in.");
    }

    @GetMapping("/session")
    public UserDto session(Authentication auth) {
        return users.findById(auth.getName())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Your session has expired. Please sign in again."));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, jwt.clearCookie().toString()).build();
    }

    // These need external providers (Google Cloud / an SMS gateway). Honest 501 until configured.
    @PostMapping("/phone/request-otp")
    public void requestOtp() { throw notConfigured("Phone sign-in"); }

    @PostMapping("/phone/verify-otp")
    public void verifyOtp() { throw notConfigured("Phone sign-in"); }

    @GetMapping("/oauth/google")
    public void google() { throw notConfigured("Google sign-in"); }

    private ApiException notConfigured(String what) {
        return new ApiException(HttpStatus.NOT_IMPLEMENTED, what + " is not configured on this server yet.");
    }
}
