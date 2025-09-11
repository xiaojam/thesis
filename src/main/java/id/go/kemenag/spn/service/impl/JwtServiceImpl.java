package id.go.kemenag.spn.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import id.go.kemenag.spn.config.custom.CustomUserDetails;
import id.go.kemenag.spn.config.property.ApplicationSettingProperty;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {

    @Autowired
    private ApplicationSettingProperty settings;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(this.settings.getSecurity().getAuth().getValue());
    }

    private long accessTtlSeconds() {
        return Optional.ofNullable(this.settings.getSecurity().getTtl()).orElse(36000L);
    }

    private long refreshTtlSeconds() {
        return Optional.ofNullable(this.settings.getSecurity().getRefreshTtl()).orElse(360000L);
    }

    @Override
    public String generateAccessToken(UserDetails user) {
        return this.generateAccessToken(this.defaultClaims(user), user);
    }

    @Override
    public String generateAccessToken(Map<String, Object> claims, UserDetails user) {
        Instant now = Instant.now();
        var customUser = (CustomUserDetails) user;

        return JWT.create()
            .withSubject(user.getUsername())
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(this.accessTtlSeconds())))
            .withClaim("roles", user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
            .withClaim("workplaceCode", customUser.getWorkplaceCode())
            .withClaim("workplaceName", customUser.getWorkplaceName())
            .withClaim("role", customUser.getRole().name())
            .withPayload(claims)
            .sign(algorithm());
    }

    @Override
    public String generateRefreshToken(UserDetails user) {
        Instant now = Instant.now();
        return JWT.create()
            .withSubject(user.getUsername())
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(now.plusSeconds(this.refreshTtlSeconds())))
            .withClaim("typ", "refresh")
            .sign(algorithm());
    }

    @Override
    public String extractUsername(String token) {
        return verify(token).getSubject();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        try {
            DecodedJWT jwt = verify(token);
            return user.getUsername().equals(jwt.getSubject())
                && jwt.getExpiresAt().toInstant().isAfter(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }

    private DecodedJWT verify(String token) {
        return JWT.require(algorithm()).build().verify(token);
    }

    private Map<String, Object> defaultClaims(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getUsername());
        return claims;
    }
}
