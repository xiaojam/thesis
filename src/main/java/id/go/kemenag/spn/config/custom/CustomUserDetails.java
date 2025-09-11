package id.go.kemenag.spn.config.custom;

import id.go.kemenag.spn.constant.AuthConstant;
import id.go.kemenag.spn.entity.UserDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private AuthConstant.Role role;
    private String workplaceCode;
    private String workplaceName;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role.name());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails fromEntity(UserDetail user) {
        return CustomUserDetails
            .builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .role(user.getRole())
            .workplaceCode(user.getWorkplaceCode())
            .workplaceName(user.getWorkplaceName())
            .build();
    }
}