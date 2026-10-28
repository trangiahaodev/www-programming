package iuh.wwwprogramming.config;

import iuh.wwwprogramming.security.ActiveAccountFilter;
import iuh.wwwprogramming.service.impl.ShopUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ShopUserDetailsService shopUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. Mở cửa hoàn toàn cho các tài nguyên tĩnh
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/IMG/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // 2. Các trang công khai ai cũng vào được
                        .requestMatchers(
                                "/",
                                "/trang-chu",
                                "/san-pham",
                                "/san-pham/**",
                                "/products",
                                "/products/**",
                                "/cart",
                                "/cart/**",
                                "/tin-tuc",
                                "/tin-tuc/**",
                                "/news",
                                "/news/**",
                                "/cam-nang",
                                "/cam-nang/**",
                                "/api/vouchers/**",
                                "/api/tracking/**",
                                "/login",
                                "/register"
                        ).permitAll()

                        // 3. Phân quyền nghiêm ngặt
                        .requestMatchers("/checkout", "/checkout/**", "/profile", "/profile/**", "/orders", "/orders/**").hasRole("CUSTOMER")
                        .requestMatchers("/admin", "/admin/**", "/api/analytics/**").hasRole("ADMIN")

                        // Các đường dẫn khác bắt buộc phải đăng nhập
                        .anyRequest().authenticated()
                )
                .userDetailsService(shopUserDetailsService)
                .formLogin(login -> login
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            boolean admin = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                            response.sendRedirect(request.getContextPath() + (admin ? "/admin/users" : "/"));
                        })
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .addFilterBefore(new ActiveAccountFilter(shopUserDetailsService), AuthorizationFilter.class)
                .csrf(csrf -> csrf.ignoringRequestMatchers("/cart/**", "/checkout/**", "/api/**", "/news/**", "/tin-tuc/**"))
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
