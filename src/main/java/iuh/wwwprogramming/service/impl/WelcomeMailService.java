package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service @Slf4j
public class WelcomeMailService {
    private final ObjectProvider<JavaMailSender> sender;
    private final boolean enabled;
    private final String from;
    public WelcomeMailService(ObjectProvider<JavaMailSender> sender,
            @Value("${app.mail.enabled:false}") boolean enabled,
            @Value("${app.mail.from:}") String from) {
        this.sender=sender; this.enabled=enabled; this.from=from;
    }
    @Async("welcomeMailExecutor")
    public void send(UserRegisteredEvent event) {
        if (!enabled) { log.info("Welcome mail disabled for user {}", event.id()); return; }
        try {
            JavaMailSender client = sender.getIfAvailable();
            if (client == null || from.isBlank()) throw new IllegalStateException("Mail configuration missing");
            var message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(event.email());
            message.setSubject("Chào mừng đến với PinkyCloud");
            message.setText("Xin chào " + event.fullName() + ",\nTài khoản PinkyCloud của bạn đã sẵn sàng. Cảm ơn bạn đã đăng ký!");
            client.send(message);
            log.info("Welcome mail sent for user {}", event.id());
        } catch (Exception ex) {
            log.warn("Welcome mail failed for user {} ({})", event.id(), ex.getClass().getSimpleName());
        }
    }
}
