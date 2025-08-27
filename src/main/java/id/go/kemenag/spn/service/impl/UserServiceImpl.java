package id.go.kemenag.spn.service.impl;

import id.go.kemenag.spn.constant.ApplicationConstant;
import id.go.kemenag.spn.service.UserService;
import id.go.kemenag.spn.util.AuthUtil;
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

    @Override
    public String test() {
        return "Hello World";
    }

    @Override
    // @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority(AuthUtil.hasRole(ApplicationConstant.Role.USER)));

        return new org.springframework.security.core.userdetails.User(
            username,
            AuthUtil.hash("@Rojam456"),
            authorities
        );
    }
}
