package iuh.wwwprogramming.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/IMG/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                // Public guest endpoints: Home, Products Catalog & Search, Product Detail
                .requestMatchers(
                    "/",
                    "/trang-chu",
                    "/san-pham",
                    "/san-pham/**",
                    "/products",
                    "/products/**",
                    "/login",
                    "/register"
                ).permitAll()
                // Admin endpoints
                .requestMatchers("/admin/**").permitAll() // For development / testing convenience, or can require ADMIN
                // Any other request
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable()) // Disabled for dev / testing form submissions
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
