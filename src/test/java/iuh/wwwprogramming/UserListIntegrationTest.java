package iuh.wwwprogramming;

import iuh.wwwprogramming.dto.UserSearchDTO;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("auth-test") @Transactional
class UserListIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository repository;
    @Autowired UserService users;
    @Autowired ShopUserDetailsService details;
    User admin;
    @BeforeEach void setup() {
        String id=UUID.randomUUID().toString();
        admin=repository.saveAndFlush(User.builder().email(id+"@example.test").userCode(id.substring(0,20))
            .fullName("Người kiểm thử " + id).password("never-return-this-hash").role("ROLE_ADMIN").active(true).build());
    }
    @Test void renderListDoesNotLeakPassword() throws Exception {
        mvc.perform(get("/admin/users").with(user(details.loadUserByUsername(admin.getEmail()))))
            .andExpect(status().isOk()).andExpect(view().name("admin/user-list"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString(admin.getEmail())))
            .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString(admin.getPassword()))));
    }
    @Test void searchByCodeNameEmailAndClampEmptyLastPage() {
        for(String key : new String[]{admin.getUserCode(),admin.getFullName(),admin.getEmail().toUpperCase()}) {
            var result=users.search(new UserSearchDTO(key,0,10));
            assertThat(result.getContent()).extracting("id").containsExactly(admin.getId());
        }
        var last=users.search(new UserSearchDTO(admin.getEmail(),20,10));
        assertThat(last.getNumber()).isZero();
        assertThat(last.getTotalElements()).isEqualTo(1);
        assertThat(users.search(new UserSearchDTO(UUID.randomUUID().toString(),0,10))).isEmpty();
    }
    @Test void invalidFiltersRemainUsable() throws Exception {
        mvc.perform(get("/admin/users").with(user(details.loadUserByUsername(admin.getEmail())))
            .param("page","-1").param("size","1000"))
            .andExpect(status().isOk()).andExpect(model().attributeExists("errorMessage"));
    }
}
