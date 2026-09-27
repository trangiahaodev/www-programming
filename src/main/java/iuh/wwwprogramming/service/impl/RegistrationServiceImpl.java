package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.UserRegisterDTO;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.event.UserRegisteredEvent;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.RegistrationService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service @RequiredArgsConstructor @Validated
@Transactional(readOnly=true)
public class RegistrationServiceImpl implements RegistrationService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher events;
    @Override @Transactional
    public void register(UserRegisterDTO dto) {
        String email = dto.getEmail().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Email này đã được sử dụng.");
        String code;
        do { code = "USR" + UUID.randomUUID().toString().replace("-", "").substring(0, 17); }
        while (users.existsByUserCode(code));
        User user = users.saveAndFlush(User.builder().email(email).fullName(dto.getFullName().trim())
            .password(encoder.encode(dto.getPassword())).userCode(code).role("ROLE_CUSTOMER").active(true).build());
        events.publishEvent(new UserRegisteredEvent(user.getId(), user.getEmail(), user.getFullName()));
    }
}
