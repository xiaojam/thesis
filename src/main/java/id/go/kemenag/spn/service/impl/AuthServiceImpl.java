package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.config.custom.CustomUserDetails;
import id.go.kemenag.spn.config.property.ApplicationSettingProperty;
import id.go.kemenag.spn.dto.auth.request.LoginRequest;
import id.go.kemenag.spn.dto.auth.request.RefreshTokenRequest;
import id.go.kemenag.spn.dto.auth.response.LoginResponse;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.service.AuthService;
import id.go.kemenag.spn.service.JwtService;
import id.go.kemenag.spn.service.RedisService;
import id.go.kemenag.spn.util.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ApplicationSettingProperty applicationSettingProperty;

    @Autowired
    private RedisService redisService;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = this.authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();

        String accessToken = this.jwtService.generateAccessToken(user);
        String refreshToken = this.jwtService.generateRefreshToken(user);

        this.redisService.save(
            "refresh:" + user.getUsername(),
            refreshToken,
            this.applicationSettingProperty.getSecurity().getRefreshTtl()
        );

        return LoginResponse
            .builder()
            .accessToken(accessToken)
            .tokenType("Bearer")
            .refreshToken(refreshToken)
            .expiresIn(this.applicationSettingProperty.getRefreshTtl())
            .build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        String username = this.jwtService.extractUsername(refreshToken);
        UserDetails user = this.userDetailsService.loadUserByUsername(username);

        if (!this.jwtService.isTokenValid(refreshToken, user)) {
            throw new BusinessErrorException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        String savedToken = this.redisService.get("refresh:" + username);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new BusinessErrorException(HttpStatus.UNAUTHORIZED, "Refresh token not recognized");
        }

        String newAccessToken = this.jwtService.generateAccessToken(user);
        String newRefreshToken = this.jwtService.generateRefreshToken(user);

        this.redisService.save(
            "refresh:" + username,
            newRefreshToken,
            this.applicationSettingProperty.getSecurity().getRefreshTtl()
        );

        return LoginResponse
            .builder()
            .accessToken(newAccessToken)
            .tokenType("Bearer")
            .refreshToken(newRefreshToken)
            .expiresIn(this.applicationSettingProperty.getRefreshTtl())
            .build();
    }

    @Override
    public void logout(String token) {
        String username = jwtService.extractUsername(token);

        redisService.delete("refresh:" + username);

        redisService.save(
            "blacklist:" + token,
            "true",
            applicationSettingProperty.getSecurity().getTtl()
        );
    }

    @Override
    public CustomUserDetails getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Auth in context = {}", auth);
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            throw new BusinessErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return (CustomUserDetails) auth.getPrincipal();
    }
}
