package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.dto.user.request.CreateUserRequest;
import id.go.kemenag.spn.dto.user.response.UserResponse;
import id.go.kemenag.spn.entity.User;
import id.go.kemenag.spn.exception.BusinessErrorException;
import id.go.kemenag.spn.repository.UserRepository;
import id.go.kemenag.spn.service.UserService;
import id.go.kemenag.spn.util.AuthUtil;
import id.go.kemenag.spn.util.ErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Set<GrantedAuthority> authorities = new HashSet<>();

        var user = this.findUserByUsername(username);

        if (user != null) {
            authorities.add(new SimpleGrantedAuthority(AuthUtil.hasRole(user.getRole())));

            return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities
            );
        }

        throw new BusinessErrorException(HttpStatus.NOT_FOUND, "User not found with username: " + username);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        if (!request.getPassword().equals(request.getVerifyPassword())) {
            ErrorUtil.throwError("Password and Verify Password do not match", HttpStatus.BAD_REQUEST);
        }

        var checkUser = this.userRepository.findByUsernameAndDeletedFalse(request.getUsername());
        if (checkUser.isPresent()) {
            ErrorUtil.throwError("Username already exists", HttpStatus.BAD_REQUEST);
        }

        var newUser = User
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

    private User findUserByUsernameAndPassword(String username, String password) {
        var passwordHash = AuthUtil.hash(password);
        return this.userRepository.findByUsernameAndPasswordAndDeletedFalse(username, passwordHash).orElse(null);
    }

    private User findUserByUsername(String username) {
        return this.userRepository.findByUsernameAndDeletedFalse(username).orElse(null);
    }
}
