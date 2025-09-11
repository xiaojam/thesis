package id.go.kemenag.spn.controller.auth;

import id.go.kemenag.spn.dto.auth.request.LoginRequest;
import id.go.kemenag.spn.dto.auth.request.RefreshTokenRequest;
import id.go.kemenag.spn.dto.auth.response.LoginResponse;
import id.go.kemenag.spn.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Auth", description = "Authentication API")
public class AuthController {

    private final AuthService authService;

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public AuthController(AuthService authService, StringRedisTemplate stringRedisTemplate) {
        this.authService = authService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return this.authService.login(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return this.authService.refreshToken(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            this.authService.logout(token);
        }
    }

    @DeleteMapping("/clear")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> clearRedis() {
        Set<String> keys = stringRedisTemplate.keys("*");
        if (!keys.isEmpty()) {
            this.stringRedisTemplate.delete(keys);
        }
        return ResponseEntity.ok("All Redis cache cleared");
    }

}
