package iuh.wwwprogramming.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record ShopPrincipal(String id, String email, String fullName, String password,
                            String role, boolean active) implements UserDetails {
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(new SimpleGrantedAuthority(role)); }
    public String getUsername() { return email; }
    public String getPassword() { return password; }
    public boolean isEnabled() { return active; }
    @Override public String toString() { return "ShopPrincipal[id=" + id + "]"; }
}
