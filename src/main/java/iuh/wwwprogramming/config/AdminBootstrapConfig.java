package iuh.wwwprogramming.config;

import iuh.wwwprogramming.service.impl.AdminBootstrapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;

@Configuration
public class AdminBootstrapConfig {
    @Bean
    @ConditionalOnProperty(name = "app.bootstrap-admin.enabled", havingValue = "true")
    CommandLineRunner bootstrapAdmin(AdminBootstrapService service,
            @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.password:}") String password) {
        return args -> service.createIfAbsent(email, password);
    }
}
