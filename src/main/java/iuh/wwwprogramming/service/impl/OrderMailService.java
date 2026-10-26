package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@Slf4j
public class OrderMailService {

    private final ObjectProvider<JavaMailSender> sender;
    private final boolean enabled;
    private final String from;

    public OrderMailService(ObjectProvider<JavaMailSender> sender,
                            @Value("${app.mail.enabled:false}") boolean enabled,
                            @Value("${app.mail.from:}") String from) {
        this.sender = sender;
        this.enabled = enabled;
        this.from = from;
    }

    @Async("welcomeMailExecutor")
    public void sendOrderConfirmation(OrderPlacedEvent event) {
        if (!enabled) {
            log.info("Order confirmation mail disabled. Skipped for order {}", event.orderCode());
            return;
        }
        if (event.customerEmail() == null || event.customerEmail().isBlank()) {
            log.info("No email provided for order {}. Skipped sending confirmation email.", event.orderCode());
            return;
        }

        try {
            JavaMailSender client = sender.getIfAvailable();
            if (client == null || from == null || from.isBlank()) {
                log.warn("Mail client not configured or 'from' address missing. Skipped email for order {}", event.orderCode());
                return;
            }

            String formattedAmount = event.totalAmount() != null
                    ? NumberFormat.getInstance(new Locale("vi", "VN")).format(event.totalAmount()) + " đ"
                    : "0 đ";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(event.customerEmail());
            message.setSubject("PinkyCloud - Xác nhận đơn hàng " + event.orderCode());
            message.setText("Xin chào " + event.customerName() + ",\n\n"
                    + "Cảm ơn bạn đã đặt hàng tại PinkyCloud!\n"
                    + "Mã đơn hàng của bạn là: " + event.orderCode() + "\n"
                    + "Số điện thoại nhận hàng: " + event.customerPhone() + "\n"
                    + "Tổng giá trị đơn hàng: " + formattedAmount + "\n\n"
                    + "Chúng tôi sẽ sớm liên hệ và giao hàng tới quý khách.\n"
                    + "Trân trọng,\nĐội ngũ PinkyCloud.");

            client.send(message);
            log.info("Order confirmation mail sent successfully for order {}", event.orderCode());
        } catch (Exception ex) {
            log.warn("Failed to send order confirmation mail for order {}: {}", event.orderCode(), ex.getMessage());
        }
    }
}
