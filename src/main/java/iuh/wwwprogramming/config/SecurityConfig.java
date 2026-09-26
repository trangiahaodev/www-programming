package iuh.wwwprogramming.config;

import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // LẤY TỪ V2: Cơ chế đọc tài khoản từ Database
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));

            String roleName = user.getRole();
            // Spring Security tự động thêm tiền tố ROLE_, nên ta cần cắt bỏ nếu trong DB đã có sẵn
            if (roleName.startsWith("ROLE_")) {
                roleName = roleName.substring(5);
            }

            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(roleName)
                    .disabled(!Boolean.TRUE.equals(user.getActive()))
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. TỪ V1: Mở cửa hoàn toàn cho các tài nguyên tĩnh
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/IMG/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // 2. GỘP V1 & V2: Các trang công khai ai cũng vào được (Trang chủ, Sản phẩm, Giỏ hàng)
                        .requestMatchers(
                                "/",
                                "/trang-chu",
                                "/san-pham",
                                "/san-pham/**",
                                "/products",
                                "/products/**",
                                "/cart/**",
                                "/login",
                                "/register"
                        ).permitAll()

                        // 3. TỪ V2: Phân quyền nghiêm ngặt
                        .requestMatchers("/checkout/**").hasRole("CUSTOMER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Các đường dẫn khác bắt buộc phải đăng nhập
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/", false) // Đăng nhập xong đẩy về trang chủ
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                // TỪ V1: Tắt CSRF tạm thời để các form POST (Thêm giỏ hàng, Checkout) hoạt động mượt mà
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}