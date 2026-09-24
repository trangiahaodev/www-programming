package iuh.wwwprogramming;

import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderItem;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class WwwProgrammingApplication {

    public static void main(String[] args) {
        SpringApplication.run(WwwProgrammingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initSampleOrders(OrderRepository orderRepository) {
        return args -> {
            if (orderRepository.count() == 0) {
                List<Order> sampleOrders = new ArrayList<>();

                // Order 1: PENDING
                Order o1 = Order.builder()
                        .orderCode("ORD26000001")
                        .customerName("Nguyễn Thị Lan")
                        .customerPhone("0987654321")
                        .shippingAddress("12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM")
                        .totalAmount(new BigDecimal("850000.00"))
                        .status(OrderStatus.PENDING)
                        .paymentMethod("COD")
                        .paymentStatus("UNPAID")
                        .build();
                OrderItem i1 = OrderItem.builder()
                        .order(o1)
                        .productName("Kem dưỡng ẩm Laneige Water Bank")
                        .productCode("SP26000001")
                        .unitPrice(new BigDecimal("450000.00"))
                        .quantity(1)
                        .subtotal(new BigDecimal("450000.00"))
                        .build();
                OrderItem i2 = OrderItem.builder()
                        .order(o1)
                        .productName("Sữa rửa mặt Cetaphil Gentle Cleanser")
                        .productCode("SP26000002")
                        .unitPrice(new BigDecimal("200000.00"))
                        .quantity(2)
                        .subtotal(new BigDecimal("400000.00"))
                        .build();
                o1.setItems(new ArrayList<>(List.of(i1, i2)));
                sampleOrders.add(o1);

                // Order 2: PROCESSING
                Order o2 = Order.builder()
                        .orderCode("ORD26000002")
                        .customerName("Trần Văn An")
                        .customerPhone("0912345678")
                        .shippingAddress("45 Lê Lợi, Q.1, TP.HCM")
                        .totalAmount(new BigDecimal("320000.00"))
                        .status(OrderStatus.PROCESSING)
                        .paymentMethod("VNPAY")
                        .paymentStatus("PAID")
                        .build();
                OrderItem i3 = OrderItem.builder()
                        .order(o2)
                        .productName("Son dưỡng môi DHC Lip Cream")
                        .productCode("SP26000003")
                        .unitPrice(new BigDecimal("160000.00"))
                        .quantity(2)
                        .subtotal(new BigDecimal("320000.00"))
                        .build();
                o2.setItems(new ArrayList<>(List.of(i3)));
                sampleOrders.add(o2);

                // Order 3: SHIPPED
                Order o3 = Order.builder()
                        .orderCode("ORD26000003")
                        .customerName("Lê Hoàng Mai")
                        .customerPhone("0933221100")
                        .shippingAddress("78 Trần Phú, Hà Đông, Hà Nội")
                        .totalAmount(new BigDecimal("1450000.00"))
                        .status(OrderStatus.SHIPPED)
                        .paymentMethod("BANKING")
                        .paymentStatus("PAID")
                        .build();
                OrderItem i4 = OrderItem.builder()
                        .order(o3)
                        .productName("Serum phục hồi da La Roche-Posay B5")
                        .productCode("SP26000004")
                        .unitPrice(new BigDecimal("725000.00"))
                        .quantity(2)
                        .subtotal(new BigDecimal("1450000.00"))
                        .build();
                o3.setItems(new ArrayList<>(List.of(i4)));
                sampleOrders.add(o3);

                // Order 4: DELIVERED
                Order o4 = Order.builder()
                        .orderCode("ORD26000004")
                        .customerName("Phạm Minh Tuấn")
                        .customerPhone("0908776655")
                        .shippingAddress("102 Hai Bà Trưng, Đà Nẵng")
                        .totalAmount(new BigDecimal("590000.00"))
                        .status(OrderStatus.DELIVERED)
                        .paymentMethod("COD")
                        .paymentStatus("PAID")
                        .build();
                OrderItem i5 = OrderItem.builder()
                        .order(o4)
                        .productName("Kem chống nắng Anessa Perfect UV")
                        .productCode("SP26000005")
                        .unitPrice(new BigDecimal("590000.00"))
                        .quantity(1)
                        .subtotal(new BigDecimal("590000.00"))
                        .build();
                o4.setItems(new ArrayList<>(List.of(i5)));
                sampleOrders.add(o4);

                // Order 5: CANCELLED
                Order o5 = Order.builder()
                        .orderCode("ORD26000005")
                        .customerName("Đỗ Thu Hà")
                        .customerPhone("0971122334")
                        .shippingAddress("15 Quang Trung, Cần Thơ")
                        .totalAmount(new BigDecimal("210000.00"))
                        .status(OrderStatus.CANCELLED)
                        .paymentMethod("COD")
                        .paymentStatus("UNPAID")
                        .build();
                OrderItem i6 = OrderItem.builder()
                        .order(o5)
                        .productName("Tẩy trang Bioderma Sensibio H2O 250ml")
                        .productCode("SP26000006")
                        .unitPrice(new BigDecimal("210000.00"))
                        .quantity(1)
                        .subtotal(new BigDecimal("210000.00"))
                        .build();
                o5.setItems(new ArrayList<>(List.of(i6)));
                sampleOrders.add(o5);

                orderRepository.saveAll(sampleOrders);
            }
        };
    }
}
