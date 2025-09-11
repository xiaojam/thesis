package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.config.custom.CustomUserDetails;
import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.dto.user.request.CreateUserRequest;
import id.go.kemenag.spn.dto.user.response.UserResponse;
import id.go.kemenag.spn.entity.UserDetail;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.repository.UserRepository;
import id.go.kemenag.spn.service.UserService;
import id.go.kemenag.spn.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = this.findUserByUsername(username);

        if (user != null) {
            return CustomUserDetails.fromEntity(user);
        }

        throw new BusinessErrorException(HttpStatus.NOT_FOUND, "User not found with username: " + username);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (!request.getPassword().equals(request.getVerifyPassword())) {
            throw new BusinessErrorException(HttpStatus.BAD_REQUEST, "Password and Verify Password do not match");
        }

        var checkUser = this.userRepository.findFirstByUsernameAndDeletedFalse(request.getUsername());
        if (checkUser.isPresent()) {
            throw new BusinessErrorException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        var newUser = UserDetail
            .builder()
            .username(request.getUsername().toLowerCase())
            .role(request.getRole())
            .password(AuthUtil.hash(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .workplaceCode(request.getWorkplaceCode())
            .workplaceName(request.getWorkplaceName())
            .build();

        var savedUser = this.userRepository.save(newUser);

        return UserResponse
            .builder()
            .username(savedUser.getUsername())
            .firstName(savedUser.getFirstName())
            .lastName(savedUser.getLastName())
            .build();
    }

    @Override
    public Boolean checkUsernameAvailability(String username) {
        return this.findUserByUsername(username) != null;
    }

    @Override
    public UserDetail findByWorkplaceCodeAndRole(String workplaceCode, AuthConstant.Role role) {
        return this.userRepository.findFirstByRoleAndWorkplaceCodeAndDeletedFalse(role, workplaceCode).orElse(null);
    }

    private UserDetail findUserByUsernameAndPassword(String username, String password) {
        var passwordHash = AuthUtil.hash(password);
        return this.userRepository.findFirstByUsernameAndPasswordAndDeletedFalse(username, passwordHash).orElse(null);
    }

    private UserDetail findUserByUsername(String username) {
        return this.userRepository.findFirstByUsernameAndDeletedFalse(username).orElse(null);
    }
}
