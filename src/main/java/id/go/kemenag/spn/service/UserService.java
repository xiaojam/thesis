package id.go.kemenag.spn.service;

import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.dto.user.request.CreateUserRequest;
import id.go.kemenag.spn.dto.user.response.UserResponse;
import id.go.kemenag.spn.entity.UserDetail;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    UserDetails loadUserByUsername(String username);

    UserResponse createUser(@Valid CreateUserRequest request);

    Boolean checkUsernameAvailability(String username);

    UserDetail findByWorkplaceCodeAndRole(String workplaceCode, AuthConstant.Role role);

    UserDetail findByUsername(String username);
}
