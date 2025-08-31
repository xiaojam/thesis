package id.go.kemenag.spn.service;

import id.go.kemenag.spn.dto.auth.request.LoginRequest;
import id.go.kemenag.spn.dto.auth.request.RefreshTokenRequest;
import id.go.kemenag.spn.dto.auth.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void logout(String token);
}
