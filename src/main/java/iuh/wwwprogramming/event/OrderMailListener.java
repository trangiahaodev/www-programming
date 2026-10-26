package iuh.wwwprogramming.event;

import iuh.wwwprogramming.service.impl.OrderMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMailListener {

    private final OrderMailService mailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlaced(OrderPlacedEvent event) {
        try {
            mailService.sendOrderConfirmation(event);
        } catch (TaskRejectedException ex) {
            log.warn("Order mail queue full for order {}", event.orderCode());
        }
    }
}
