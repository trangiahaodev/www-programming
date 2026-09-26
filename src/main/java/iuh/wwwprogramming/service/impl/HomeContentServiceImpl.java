package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.NewsDTO;
import iuh.wwwprogramming.dto.OfficeDTO;
import iuh.wwwprogramming.dto.ProductCardDTO;
import iuh.wwwprogramming.dto.VoucherDTO;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.HomeContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeContentServiceImpl implements HomeContentService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final iuh.wwwprogramming.service.NewsService newsService;
    private final iuh.wwwprogramming.repository.VoucherRepository voucherRepository;

    @Override
    public List<OfficeDTO> getOffices() {
        return List.of(
                OfficeDTO.builder()
                        .id("office-001")
                        .title("Văn phòng chính (Showroom TP. HCM)")
                        .address("Số 57, đường Quang Trung, quận Gò Vấp, TP. HCM")
                        .description("Không gian trải nghiệm làm đẹp chuẩn quốc tế với công nghệ soi da AI 3D, tư vấn routine chăm sóc cá nhân hóa cùng chuyên viên.")
                        .city("TP. HCM")
                        .phone("(028) 1234 5678")
                        .email("pinkycloudvietnam@gmail.com")
                        .workingHours("08:00 - 21:00 (Thứ 2 - Chủ nhật)")
                        .supportType("Trực tiếp tại Showroom")
                        .coupon("PINKY_GOVAP")
                        .perks(List.of(
                                "🔍 Soi da AI và thiết lập skincare routine miễn phí",
                                "🎁 Tặng check-in giftbox 3 món mẫu thử mini",
                                "🧴 Trải nghiệm trực tiếp mẫu test full-size tại quầy"
                        ))
                        .image("https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=1200&q=80")
                        .build(),

                OfficeDTO.builder()
                        .id("office-002")
                        .title("Chi nhánh Hà Nội (Showroom Hoàn Kiếm)")
                        .address("Số 12, phố Hàng Bài, quận Hoàn Kiếm, Hà Nội")
                        .description("Showroom tọa lạc tại vị trí đắc địa trung tâm thủ đô, cập nhật liên tục các bộ sưu tập mỹ phẩm chính hãng xu hướng mới nhất.")
                        .city("Hà Nội")
                        .phone("(024) 9988 7766")
                        .email("hanoi@pinkycloud.vn")
                        .workingHours("08:30 - 21:30 (Thứ 2 - Chủ nhật)")
                        .supportType("Trực tiếp tại Showroom")
                        .coupon("PINKY_HANOI")
                        .perks(List.of(
                                "🔍 Đo độ ẩm và đánh giá hàng rào lipid da",
                                "🎁 Nhận sample kit dưỡng ẩm lành tính miễn phí",
                                "✨ Voucher ưu đãi 5% cho hóa đơn mua sắm tiếp theo"
                        ))
                        .image("https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=1200&q=80")
                        .build(),

                OfficeDTO.builder()
                        .id("office-003")
                        .title("Trung tâm CSKH & Tư vấn Trực Tuyến")
                        .address("Hệ thống Hỗ trợ Khách hàng Trực tuyến Toàn Quốc")
                        .description("Kênh tư vấn da liễu trực tuyến 24/7 hoàn toàn miễn phí, giao hàng siêu tốc và hỗ trợ đổi trả trong 30 ngày an tâm tuyệt đối.")
                        .city("Toàn quốc")
                        .phone("1900 633 950")
                        .email("support@pinkycloud.vn")
                        .workingHours("24/7 (Toàn thời gian)")
                        .supportType("Tư vấn Online & Hotline")
                        .coupon("PINKY_ONLINE")
                        .perks(List.of(
                                "📞 Chat 1:1 cùng chuyên viên da liễu 24/7",
                                "🎟️ Nhận e-voucher giảm thêm 50k mua online",
                                "🚚 Miễn phí vận chuyển toàn quốc đơn từ 299.000₫"
                        ))
                        .image("https://images.unsplash.com/photo-1519014816548-bf5fe059798b?auto=format&fit=crop&w=1200&q=80")
                        .build()
        );
    }

    @Override
    public List<NewsDTO> getFeaturedNews(int limit) {
        return newsService.getAllNews(null, null).stream()
                .limit(Math.max(1, limit))
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> getActiveVouchers() {
        List<iuh.wwwprogramming.entity.Voucher> dbVouchers = voucherRepository.findByActiveTrue();
        if (dbVouchers != null && !dbVouchers.isEmpty()) {
            return dbVouchers.stream().map(v -> VoucherDTO.builder()
                    .id(v.getId().intValue())
                    .title(v.getTitle())
                    .code(v.getCode())
                    .detail(v.getDetail())
                    .status(v.getStatus())
                    .accent(v.getAccent())
                    .build()
            ).collect(Collectors.toList());
        }

        return getFallbackVouchers();
    }

    private List<VoucherDTO> getFallbackVouchers() {
        return List.of(
                VoucherDTO.builder()
                        .id(1)
                        .title("Giảm 15% toàn bộ đơn hàng")
                        .code("PINKY15")
                        .detail("Áp dụng cho đơn từ 499.000₫")
                        .status("active")
                        .accent("linear-gradient(135deg, #fff1f5 0%, #ffd6e3 100%)")
                        .build(),
                VoucherDTO.builder()
                        .id(2)
                        .title("Freeship toàn quốc")
                        .code("FREESHIP")
                        .detail("Cho đơn từ 299.000₫")
                        .status("active")
                        .accent("linear-gradient(135deg, #fff8df 0%, #ffe38a 100%)")
                        .build(),
                VoucherDTO.builder()
                        .id(3)
                        .title("Giảm 50.000₫ makeup")
                        .code("HOTDEAL")
                        .detail("Số lượng voucher có hạn mỗi ngày")
                        .status("active")
                        .accent("linear-gradient(135deg, #eef7ff 0%, #cde8ff 100%)")
                        .build(),
                VoucherDTO.builder()
                        .id(4)
                        .title("Giảm 10% dòng dưỡng da")
                        .code("SKINCARE10")
                        .detail("Áp dụng cho mọi khách hàng mới")
                        .status("active")
                        .accent("linear-gradient(135deg, #f3e5f5 0%, #e1bee7 100%)")
                        .build()
        );
    }

    @Override
    public long getTotalActiveProducts() {
        return productRepository.count();
    }

    @Override
    public long getTotalCategories() {
        return categoryRepository.count();
    }

    private List<ProductCardDTO> findLinkedProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) {
            // Fallback to any 2 active products if IDs do not match
            products = productRepository.findAll(PageRequest.of(0, 2)).getContent();
        }

        return products.stream().map(p -> {
            int discount = (p.getDiscount() != null) ? p.getDiscount() : 0;
            double price = (p.getPrice() != null) ? p.getPrice().doubleValue() : 0.0;
            Double origPrice = (discount > 0 && price > 0)
                    ? (double) Math.round(price / (1.0 - (discount / 100.0)))
                    : null;

            return ProductCardDTO.builder()
                    .id(p.getId())
                    .productCode(p.getProductCode())
                    .name(p.getName())
                    .brand(p.getBrand())
                    .categoryName(p.getCategory() != null ? p.getCategory().getName() : "")
                    .image(p.getImage())
                    .price(price)
                    .discount(discount)
                    .originalPrice(origPrice)
                    .rating(p.getRating() != null ? p.getRating() : 5.0)
                    .reviewCount(p.getReviewCount() != null ? p.getReviewCount() : 0)
                    .build();
        }).collect(Collectors.toList());
    }
}
