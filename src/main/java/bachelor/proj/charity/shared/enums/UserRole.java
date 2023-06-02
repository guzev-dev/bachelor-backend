package bachelor.proj.charity.shared.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public enum UserRole {
    USER(
            Set.of(new SimpleGrantedAuthority("USER_AUTHORITY"))
    ),
    FUND_MODERATOR(
            Set.of(new SimpleGrantedAuthority("USER_AUTHORITY"),
                    new SimpleGrantedAuthority("FUND_MODERATOR_AUTHORITY"))
    ),
    ADMIN(
            Set.of(new SimpleGrantedAuthority("USER_AUTHORITY"),
                    new SimpleGrantedAuthority("FUND_MODERATOR_AUTHORITY"),
                    new SimpleGrantedAuthority("ADMINISTRATOR_AUTHORITY"))
    );

    private final Set<SimpleGrantedAuthority> authorities;

    UserRole(Set<SimpleGrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }
}
