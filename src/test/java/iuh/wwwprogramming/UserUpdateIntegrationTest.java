package iuh.wwwprogramming;

import iuh.wwwprogramming.dto.UserUpdateDTO;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.UserService;
import iuh.wwwprogramming.service.impl.ShopUserDetailsService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("auth-test") @Transactional
class UserUpdateIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository repository;
    @Autowired UserService users;
    @Autowired ShopUserDetailsService details;
    User admin, customer;
    @BeforeEach void setup() { admin=account("ROLE_ADMIN"); customer=account("ROLE_CUSTOMER"); }
    User account(String role) {
        String id=UUID.randomUUID().toString();
        return repository.saveAndFlush(User.builder().email(id+"@example.test").userCode(id.substring(0,20))
            .fullName("Test User").password("unchanged-hash").role(role).active(true).build());
    }
    UserUpdateDTO form(User target, boolean active) {
        return new UserUpdateDTO("New Name", target.getEmail(), "+84 912345678", "Địa chỉ mới", active);
    }
    @Test void editRendersAndUpdateCannotChangePasswordOrRole() throws Exception {
        var principal=details.loadUserByUsername(admin.getEmail());
        mvc.perform(get("/admin/users/"+customer.getId()+"/edit").with(user(principal)))
            .andExpect(status().isOk()).andExpect(view().name("admin/user-edit"));
        mvc.perform(post("/admin/users/"+customer.getId()+"/edit").with(user(principal)).with(csrf())
            .param("fullName","Updated Customer").param("email",customer.getEmail()).param("active","false")
            .param("role","ROLE_ADMIN").param("password","hacked"))
            .andExpect(redirectedUrl("/admin/users")).andExpect(flash().attributeExists("successMessage"));
        var saved=repository.findById(customer.getId()).orElseThrow();
        assertThat(saved.getFullName()).isEqualTo("Updated Customer");
        assertThat(saved.getActive()).isFalse();
        assertThat(saved.getRole()).isEqualTo("ROLE_CUSTOMER");
        assertThat(saved.getPassword()).isEqualTo("unchanged-hash");
    }
    @Test void invalidFormShowsFieldErrorsAndMissingUserRedirects() throws Exception {
        var principal=details.loadUserByUsername(admin.getEmail());
        mvc.perform(post("/admin/users/"+customer.getId()+"/edit").with(user(principal)).with(csrf())
            .param("fullName"," ").param("email","invalid").param("active","true"))
            .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("userForm","email","fullName"));
        mvc.perform(get("/admin/users/missing/edit").with(user(principal)))
            .andExpect(redirectedUrl("/admin/users")).andExpect(flash().attributeExists("errorMessage"));
    }
    @Test void duplicateEmailIsRejected() {
        var dto=form(customer,true); dto.setEmail(admin.getEmail().toUpperCase());
        assertThatThrownBy(()->users.update(customer.getId(),dto,admin.getId()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Email");
    }
    @Test void cannotLockSelf() {
        assertThatThrownBy(()->users.update(admin.getId(),form(admin,false),admin.getId()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("chính tài khoản");
    }
    @Test void cannotLockLastActiveAdmin() {
        assertThatThrownBy(()->users.update(admin.getId(),form(admin,false),UUID.randomUUID().toString()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cuối cùng");
    }
    @Test void canUnlockAndLockedUserCannotReuseSession() throws Exception {
        var principal=details.loadUserByUsername(customer.getEmail());
        users.update(customer.getId(),form(customer,false),admin.getId());
        mvc.perform(get("/").with(user(principal))).andExpect(redirectedUrl("/login?expired"));
        users.update(customer.getId(),form(customer,true),admin.getId());
        assertThat(repository.findById(customer.getId()).orElseThrow().getActive()).isTrue();
    }
}
