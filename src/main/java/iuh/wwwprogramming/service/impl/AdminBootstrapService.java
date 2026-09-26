package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBootstrapService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    @Transactional
    public void createIfAbsent(String email, String password) {
        if (email == null || !email.matches("[^\\s@]+@[^\\s@]+[.][^\\s@]+") || password == null
                || password.length() < 8 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Cần cấu hình email và mật khẩu Admin hợp lệ (8 ký tự trở lên, tối đa 72 byte).");
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(normalized)) return;
        users.save(User.builder().email(normalized).password(encoder.encode(password))
            .userCode("USR" + UUID.randomUUID().toString().replace("-", "").substring(0, 17))
            .fullName("Quản trị viên").role("ROLE_ADMIN").active(true).build());
    }
}
