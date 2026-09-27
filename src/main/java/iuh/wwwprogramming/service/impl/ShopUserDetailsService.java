package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.security.ShopPrincipal;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public UserDetails loadUserByUsername(String email) {
        var user = users.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT))
            .orElseThrow(() -> new UsernameNotFoundException("Thông tin đăng nhập không hợp lệ"));
        return new ShopPrincipal(user.getId(), user.getEmail(), user.getFullName(), user.getPassword(),
            user.getRole(), Boolean.TRUE.equals(user.getActive()));
    }
    public boolean isCurrent(ShopPrincipal principal) {
        return users.findById(principal.id()).filter(u -> Boolean.TRUE.equals(u.getActive())
            && u.getEmail().equals(principal.email()) && u.getRole().equals(principal.role())
            && u.getPassword().equals(principal.password())).isPresent();
    }
}
