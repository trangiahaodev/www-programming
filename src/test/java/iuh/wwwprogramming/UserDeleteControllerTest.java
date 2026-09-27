package iuh.wwwprogramming;

import iuh.wwwprogramming.config.SecurityConfig;
import iuh.wwwprogramming.controller.AdminUserController;
import iuh.wwwprogramming.security.ShopPrincipal;
import iuh.wwwprogramming.service.UserService;
import iuh.wwwprogramming.service.impl.ShopUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class) @Import(SecurityConfig.class)
class UserDeleteControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean UserService users;
    @MockitoBean ShopUserDetailsService details;
    @Test void foreignKeyRaceIsHandledWithPrg() throws Exception {
        var principal=new ShopPrincipal("admin", "admin@example.test", "Admin", "hash", "ROLE_ADMIN", true);
        when(details.isCurrent(principal)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("simulated FK race")).when(users).delete(eq("target"),any(),eq("admin"));
        mvc.perform(post("/admin/users/target/delete").with(user(principal)).with(csrf()).param("confirmed","true"))
            .andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("errorMessage"));
        verify(users).delete(eq("target"),any(),eq("admin"));
    }
    @Test void customerCannotSubmitDelete() throws Exception {
        var principal=new ShopPrincipal("customer", "customer@example.test", "Customer", "hash", "ROLE_CUSTOMER", true);
        when(details.isCurrent(principal)).thenReturn(true);
        mvc.perform(post("/admin/users/target/delete").with(user(principal)).with(csrf()).param("confirmed","true"))
            .andExpect(status().isForbidden());
        verifyNoInteractions(users);
    }
}
