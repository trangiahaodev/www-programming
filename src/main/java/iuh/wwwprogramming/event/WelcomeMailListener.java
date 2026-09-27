package iuh.wwwprogramming.event;

import iuh.wwwprogramming.service.impl.WelcomeMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.*;

@Component @RequiredArgsConstructor @Slf4j
public class WelcomeMailListener {
    private final WelcomeMailService mail;
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void registered(UserRegisteredEvent event) {
        try { mail.send(event); }
        catch (TaskRejectedException ex) { log.warn("Welcome mail queue full for user {}", event.id()); }
    }
}
