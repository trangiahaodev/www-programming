package iuh.wwwprogramming.config;

import iuh.wwwprogramming.security.ActiveAccountFilter;
import iuh.wwwprogramming.service.impl.ShopUserDetailsService;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class SecurityConfig {
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ShopUserDetailsService users) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/IMG/**", "/favicon.ico", "/error").permitAll()
                .requestMatchers("/", "/trang-chu", "/san-pham", "/san-pham/**", "/products", "/products/**",
                    "/cart", "/cart/**", "/login", "/register").permitAll()
                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                .requestMatchers("/checkout", "/checkout/**", "/profile", "/profile/**", "/orders", "/orders/**").hasRole("CUSTOMER")
                .anyRequest().denyAll())
            .formLogin(login -> login.loginPage("/login").usernameParameter("email")
                .successHandler((request, response, authentication) -> {
                    boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    response.sendRedirect(request.getContextPath() + (admin ? "/admin/users" : "/"));
                }).failureUrl("/login?error").permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/login?logout"))
            .addFilterBefore(new ActiveAccountFilter(users), AuthorizationFilter.class);
        return http.build();
    }
}
