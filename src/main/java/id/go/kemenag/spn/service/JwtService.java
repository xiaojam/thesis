package id.go.kemenag.spn.service;


import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    String generateAccessToken(UserDetails user);

    String generateAccessToken(Map<String, Object> claims, UserDetails user);

    String generateRefreshToken(UserDetails user);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails user);
}
