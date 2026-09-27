package iuh.wwwprogramming;

import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.security.ShopPrincipal;
import iuh.wwwprogramming.service.impl.AdminBootstrapService;
import iuh.wwwprogramming.service.impl.ShopUserDetailsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("auth-test")
@Transactional
class AuthLoginIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    @Autowired ShopUserDetailsService details;
    @Autowired AdminBootstrapService bootstrap;
    User customer;
    User admin;

    @BeforeEach void setup() {
        customer = account("ROLE_CUSTOMER");
        admin = account("ROLE_ADMIN");
    }
    User account(String role) {
        String suffix = UUID.randomUUID().toString();
        return users.saveAndFlush(User.builder().email(suffix + "@example.test").fullName("Test User")
            .userCode(suffix.substring(0, 20)).password(encoder.encode("Password123!"))
            .role(role).active(true).build());
    }
    @Test void loginRendersAndCustomerLogsIn() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("_csrf")));
        mvc.perform(post("/login").with(csrf()).param("email", customer.getEmail().toUpperCase())
            .param("password", "Password123!")).andExpect(authenticated()).andExpect(redirectedUrl("/"));
    }
    @Test void adminDestinationAndProtectedRoutes() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("email", admin.getEmail()).param("password", "Password123!"))
            .andExpect(authenticated()).andExpect(redirectedUrl("/admin/users"));
        mvc.perform(get("/admin/users")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/admin/users").with(user(details.loadUserByUsername(customer.getEmail())))).andExpect(status().isForbidden());
        mvc.perform(get("/admin/categories").with(user(details.loadUserByUsername(admin.getEmail())))).andExpect(status().isOk());
        mvc.perform(get("/admin/products").with(user(details.loadUserByUsername(admin.getEmail())))).andExpect(status().isOk());
    }
    @Test void badPasswordAndLockedAccountFail() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("email", customer.getEmail()).param("password", "wrong"))
            .andExpect(unauthenticated()).andExpect(redirectedUrl("/login?error"));
        customer.setActive(false); users.saveAndFlush(customer);
        mvc.perform(post("/login").with(csrf()).param("email", customer.getEmail()).param("password", "Password123!"))
            .andExpect(unauthenticated()).andExpect(redirectedUrl("/login?error"));
    }
    @Test void csrfRequiredForLoginAndAdminMutation() throws Exception {
        mvc.perform(post("/login").param("email", customer.getEmail()).param("password", "Password123!"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/admin/categories/create").with(user(details.loadUserByUsername(admin.getEmail()))))
            .andExpect(status().isForbidden());
    }
    @Test void accountLockAndDeletionInvalidateExistingPrincipal() throws Exception {
        ShopPrincipal principal = (ShopPrincipal) details.loadUserByUsername(customer.getEmail());
        customer.setActive(false); users.saveAndFlush(customer);
        mvc.perform(get("/").with(user(principal))).andExpect(redirectedUrl("/login?expired"));
        users.delete(customer); users.flush();
        mvc.perform(get("/").with(user(principal))).andExpect(redirectedUrl("/login?expired"));
    }
    @Test void logoutRequiresPostAndClearsAuthentication() throws Exception {
        mvc.perform(post("/logout").with(csrf()).with(user(details.loadUserByUsername(customer.getEmail()))))
            .andExpect(unauthenticated()).andExpect(redirectedUrl("/login?logout"));
    }
    @Test void bootstrapDoesNotOverwriteExistingAccount() {
        bootstrap.createIfAbsent(customer.getEmail(), "DifferentPassword!");
        assertThat(users.findById(customer.getId()).orElseThrow().getRole()).isEqualTo("ROLE_CUSTOMER");
        assertThat(encoder.matches("Password123!", customer.getPassword())).isTrue();
    }
}
