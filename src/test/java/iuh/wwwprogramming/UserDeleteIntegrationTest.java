package iuh.wwwprogramming;

import iuh.wwwprogramming.dto.UserDeleteDTO;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.repository.*;
import iuh.wwwprogramming.service.UserService;
import iuh.wwwprogramming.service.impl.ShopUserDetailsService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("auth-test") @Transactional
class UserDeleteIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository repository;
    @Autowired OrderRepository orders;
    @Autowired UserService users;
    @Autowired ShopUserDetailsService details;
    User admin, customer;
    @BeforeEach void setup() { admin=account("ROLE_ADMIN"); customer=account("ROLE_CUSTOMER"); }
    User account(String role) {
        String id=UUID.randomUUID().toString();
        return repository.saveAndFlush(User.builder().email(id+"@example.test").userCode(id.substring(0,20))
            .fullName("Test <script> User").password("test-hash").role(role).active(true).build());
    }
    UserDeleteDTO form() { return new UserDeleteDTO(true,"",0,10); }

    @Test void deleteWithoutOrdersUsesPrgAndInvalidatesPrincipal() throws Exception {
        var oldPrincipal=details.loadUserByUsername(customer.getEmail());
        mvc.perform(post("/admin/users/"+customer.getId()+"/delete").with(csrf())
            .with(user(details.loadUserByUsername(admin.getEmail()))).param("confirmed","true")
            .param("keyword","sample").param("page","2").param("size","10"))
            .andExpect(redirectedUrl("/admin/users?keyword=sample&page=2&size=10"))
            .andExpect(flash().attributeExists("successMessage"));
        assertThat(repository.existsById(customer.getId())).isFalse();
        mvc.perform(get("/").with(user(oldPrincipal))).andExpect(redirectedUrl("/login?expired"));
    }
    @ParameterizedTest
    @ValueSource(strings={"PENDING","CONFIRMED","SHIPPING","DELIVERED","CANCELLED","CANCELED"})
    void anyOrderStatusPreventsDeletionInService(String status) {
        String id=UUID.randomUUID().toString();
        var order=orders.saveAndFlush(Order.builder().orderCode(id.substring(0,20)).user(customer)
            .recipientName("Test").recipientPhone("0900000000").shippingAddress("Test address")
            .totalAmount(BigDecimal.ONE).paymentMethod("COD").status(status).build());
        assertThatThrownBy(()->users.delete(customer.getId(),form(),admin.getId()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("đơn hàng");
        assertThat(repository.existsById(customer.getId())).isTrue();
        assertThat(orders.existsById(order.getId())).isTrue();
    }
    @Test void missingConfirmationAndCsrfCannotDelete() throws Exception {
        var principal=details.loadUserByUsername(admin.getEmail());
        mvc.perform(post("/admin/users/"+customer.getId()+"/delete").with(csrf()).with(user(principal)))
            .andExpect(redirectedUrl("/admin/users")).andExpect(flash().attributeExists("errorMessage"));
        mvc.perform(post("/admin/users/"+customer.getId()+"/delete").with(user(principal)).param("confirmed","true"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/admin/users/"+customer.getId()+"/delete").with(user(principal)))
            .andExpect(status().isMethodNotAllowed());
        assertThat(repository.existsById(customer.getId())).isTrue();
    }
    @Test void doubleSubmissionAndMissingUserReturnFlashError() throws Exception {
        var principal=details.loadUserByUsername(admin.getEmail());
        users.delete(customer.getId(),form(),admin.getId());
        mvc.perform(post("/admin/users/"+customer.getId()+"/delete").with(csrf()).with(user(principal)).param("confirmed","true"))
            .andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("errorMessage"));
    }
    @Test void selfDeleteIsBlocked() {
        assertThatThrownBy(()->users.delete(admin.getId(),form(),admin.getId()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("chính tài khoản");
    }
    @Test void lastAdminCannotBeDeleted() {
        assertThatThrownBy(()->users.delete(admin.getId(),form(),UUID.randomUUID().toString()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("cuối cùng");
    }
    @Test void listContainsOneInlineDialogWithCsrfAndEscapedName() throws Exception {
        mvc.perform(get("/admin/users").with(user(details.loadUserByUsername(admin.getEmail()))))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"deleteUserDialog\"")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"_csrf\"")))
            .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Test <script> User"))));
    }
}
