package cinema.security;

import cinema.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // Hàm build để chuyển từ User Entity sang UserDetails
    public static UserDetailsImpl build(User user) {
        // Lấy Role của User biến thành Quyền hạn (Authority) cho Spring hiểu
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));

        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return email; } // Dùng Email làm Username
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; } // Thực tế có thể lấy user.getIsActive()
}