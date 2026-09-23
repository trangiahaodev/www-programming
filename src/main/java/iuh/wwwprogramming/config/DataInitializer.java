package iuh.wwwprogramming.config;

import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Khởi tạo tài khoản Customer mẫu để kiểm thử tính năng Checkout
            if (!userRepository.existsByEmail("khachhang@pinkycloud.com")) {
                User customer = User.builder()
                        .userCode("KH26000001")
                        .email("khachhang@pinkycloud.com")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Nguyễn Thu Hà")
                        .phone("0912345678")
                        .address("12 Nguyễn Văn Bảo, Phường 4, Quận Gò Vấp, TP. Hồ Chí Minh")
                        .role("ROLE_CUSTOMER")
                        .active(true)
                        .build();
                userRepository.save(customer);
            }

            // Khởi tạo tài khoản Admin mẫu
            if (!userRepository.existsByEmail("admin@pinkycloud.com")) {
                User admin = User.builder()
                        .userCode("AD26000001")
                        .email("admin@pinkycloud.com")
                        .password(passwordEncoder.encode("admin123"))
                        .fullName("Quản Trị Viên Pinky")
                        .phone("0988888888")
                        .address("Hệ Thống Pinky Cloud")
                        .role("ROLE_ADMIN")
                        .active(true)
                        .build();
                userRepository.save(admin);
            }

            // Khởi tạo sản phẩm mẫu để có hàng test giỏ và đặt hàng
            productService.initSampleProductsIfEmpty();
        };
    }
}
