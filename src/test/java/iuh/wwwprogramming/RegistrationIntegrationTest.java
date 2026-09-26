package iuh.wwwprogramming;

import iuh.wwwprogramming.dto.UserRegisterDTO;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.RegistrationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.UUID;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"app.mail.enabled=true", "app.mail.from=test@example.test"})
@AutoConfigureMockMvc
@ActiveProfiles("auth-test")
class RegistrationIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired RegistrationService registration;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;
    @Autowired PlatformTransactionManager transactions;
    @MockitoBean JavaMailSender sender;
    String email;
    @BeforeEach void setup() { email = UUID.randomUUID() + "@example.test"; }
    @AfterEach void cleanup() { users.findByEmailIgnoreCase(email).ifPresent(users::delete); }
    UserRegisterDTO dto() { return new UserRegisterDTO("Test Customer", email, "Password123!", "Password123!"); }

    @Test void registrationHashesPasswordIgnoresForgedRoleAndUsesPrg() throws Exception {
        mvc.perform(get("/register")).andExpect(status().isOk());
        mvc.perform(post("/register").with(csrf()).param("fullName", "Test Customer")
            .param("email", email.toUpperCase()).param("password", "Password123!")
            .param("confirmPassword", "Password123!").param("role", "ROLE_ADMIN").param("active", "false"))
            .andExpect(redirectedUrl("/login")).andExpect(flash().attributeExists("successMessage"));
        var user = users.findByEmailIgnoreCase(email).orElseThrow();
        assertThat(user.getRole()).isEqualTo("ROLE_CUSTOMER");
        assertThat(user.getActive()).isTrue();
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isNotEqualTo("Password123!");
        assertThat(encoder.matches("Password123!",user.getPassword())).isTrue();
        verify(sender, timeout(3000)).send(any(SimpleMailMessage.class));
        mvc.perform(post("/register").with(csrf()).param("fullName", "Test Customer")
            .param("email", email.toUpperCase()).param("password", "Password123!").param("confirmPassword", "Password123!"))
            .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("registration", "email"))
            .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Password123!"))));
    }
    @Test void invalidDataAndCsrfCannotCreateAccount() throws Exception {
        mvc.perform(post("/register").with(csrf()).param("fullName", " ").param("email", email)
            .param("password", "short").param("confirmPassword", "different"))
            .andExpect(status().isOk()).andExpect(model().attributeHasErrors("registration"));
        mvc.perform(post("/register").param("email", email)).andExpect(status().isForbidden());
        assertThat(users.existsByEmailIgnoreCase(email)).isFalse();
        verifyNoInteractions(sender);
    }
    @Test void slowSmtpDoesNotBlockRegistration() throws Exception {
        CountDownLatch sending = new CountDownLatch(1), release = new CountDownLatch(1), completed = new CountDownLatch(1);
        doAnswer(call -> { sending.countDown(); try { release.await(10, TimeUnit.SECONDS); } finally { completed.countDown(); } return null; })
            .when(sender).send(any(SimpleMailMessage.class));
        ExecutorService client = Executors.newSingleThreadExecutor();
        try {
            Future<?> result = client.submit(() -> registration.register(dto()));
            assertThat(sending.await(5,TimeUnit.SECONDS)).isTrue();
            result.get(2,TimeUnit.SECONDS);
            assertThat(users.existsByEmailIgnoreCase(email)).isTrue();
        } finally { release.countDown(); completed.await(5,TimeUnit.SECONDS); client.shutdownNow(); }
    }
    @Test void smtpFailureKeepsAccount() {
        doThrow(new MailSendException("Fake SMTP failure")).when(sender).send(any(SimpleMailMessage.class));
        registration.register(dto());
        verify(sender, timeout(3000)).send(any(SimpleMailMessage.class));
        assertThat(users.existsByEmailIgnoreCase(email)).isTrue();
    }
    @Test void rollbackDoesNotSendMail() {
        new TransactionTemplate(transactions).executeWithoutResult(status -> {
            registration.register(dto());
            status.setRollbackOnly();
        });
        assertThat(users.existsByEmailIgnoreCase(email)).isFalse();
        verifyNoInteractions(sender);
    }
}
