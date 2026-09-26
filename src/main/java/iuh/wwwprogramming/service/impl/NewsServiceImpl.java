package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.NewsDTO;
import iuh.wwwprogramming.dto.ProductCardDTO;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsServiceImpl implements NewsService {

    private final ProductRepository productRepository;
    private final iuh.wwwprogramming.repository.NewsArticleRepository newsArticleRepository;

    private static final Map<String, String> RECOMMENDATION_REASONS = Map.of(
            "pc-016", "Chứa 100% chiết xuất rau má vùng Madagascar giúp làm dịu nhanh da kích ứng, kháng viêm và đẩy nhanh quá trình phục hồi các tổn thương sau mụn.",
            "pc-009", "Độ pH lý tưởng 5.5 nhẹ dịu cùng tinh dầu tràm trà tự nhiên giúp kiểm soát bã nhờn hiệu quả, kháng khuẩn và bảo vệ hàng rào ẩm tự nhiên.",
            "pc-001", "Công nghệ chống nắng tối tân bảo vệ da nhạy cảm toàn diện trước tia UV với kết cấu dạng sữa lỏng thoáng mịn, không để lại màng trắng hay bết rít.",
            "pc-012", "Sữa rửa mặt dịu lành không chứa xà phòng, giúp làm sạch sâu bụi mịn mà vẫn duy trì độ ẩm tự nhiên, được các chuyên gia khuyên dùng hàng đầu.",
            "pc-013", "Cấp nước sâu vào các tầng da với 5 loại phân tử Hyaluronic Acid, giảm căng thẳng cho biểu bì và mang lại làn da căng mướt ngậm nước tức thì.",
            "pc-050", "Sự kết hợp giữa Hyaluronic Acid 2% và Vitamin B5 giúp liên kết phân tử nước giữ ẩm cho tế bào da, thúc đẩy quá trình phục hồi biểu bì bị tổn thương.",
            "pc-040", "Mặt nạ ngủ môi giàu Vitamin C và chất chống oxy hóa từ quả mọng, giúp loại bỏ tế bào chết môi ẩm mượt và hồng hào rạng rỡ sau mỗi giấc ngủ.",
            "pc-046", "Kem dưỡng phục hồi da mụn với phức hợp AHA-BHA-PHA và 70% chiết xuất rau má, củng cố hàng rào bảo vệ da khỏe mạnh rõ rệt chỉ sau 14 ngày.",
            "pc-049", "Niacinamide nồng độ 10% giúp cải thiện rõ rệt tình trạng lỗ chân lông to, làm mờ thâm sạm và khôi phục bề mặt da mịn màng, sáng khỏe.",
            "pc-052", "Vitamin C tươi nguyên chất giúp dưỡng sáng da, mờ vết thâm hiệu quả mà vẫn cực kỳ dịu nhẹ, phù hợp cho mọi làn da sử dụng hàng ngày."
    );

    @Override
    public List<NewsDTO> getAllNews(String category, String keyword) {
        List<iuh.wwwprogramming.entity.NewsArticle> dbArticles = newsArticleRepository.searchArticles(category, keyword);
        if (!dbArticles.isEmpty()) {
            return dbArticles.stream().map(this::convertToDTO).collect(Collectors.toList());
        }

        if (newsArticleRepository.count() == 0) {
            return fallbackGetAllNews(category, keyword);
        }
        return List.of();
    }

    @Override
    public NewsDTO getNewsBySlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            return null;
        }
        String clean = slug.trim();

        Optional<iuh.wwwprogramming.entity.NewsArticle> articleOpt = newsArticleRepository.findBySlugAndActiveTrue(clean);
        if (articleOpt.isPresent()) {
            return convertToDTO(articleOpt.get());
        }

        try {
            Long id = Long.parseLong(clean);
            Optional<iuh.wwwprogramming.entity.NewsArticle> byId = newsArticleRepository.findById(id);
            if (byId.isPresent() && Boolean.TRUE.equals(byId.get().getActive())) {
                return convertToDTO(byId.get());
            }
        } catch (NumberFormatException ignored) {}

        if (newsArticleRepository.count() == 0) {
            return fallbackGetNewsBySlug(clean);
        }
        return null;
    }

    @Override
    public NewsDTO getNewsById(Integer id) {
        if (id == null) return null;
        return newsArticleRepository.findById(id.longValue())
                .filter(a -> Boolean.TRUE.equals(a.getActive()))
                .map(this::convertToDTO)
                .orElseGet(() -> {
                    if (newsArticleRepository.count() == 0) {
                        return buildRawArticles().stream().filter(a -> id.equals(a.getId())).findFirst().orElse(null);
                    }
                    return null;
                });
    }

    @Override
    public List<NewsDTO> getRelatedNews(Integer currentId, String category, int limit) {
        List<iuh.wwwprogramming.entity.NewsArticle> all = newsArticleRepository.findAllByActiveTrueOrderByPublishedDateDesc();
        if (all.isEmpty() && newsArticleRepository.count() == 0) {
            return fallbackGetRelatedNews(currentId, category, limit);
        }

        return all.stream()
                .filter(a -> currentId == null || !a.getId().equals(currentId.longValue()))
                .sorted((a, b) -> {
                    boolean aSame = category != null && category.equalsIgnoreCase(a.getCategory());
                    boolean bSame = category != null && category.equalsIgnoreCase(b.getCategory());
                    if (aSame && !bSame) return -1;
                    if (!aSame && bSame) return 1;
                    return Long.compare(b.getId(), a.getId());
                })
                .limit(Math.max(1, limit))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getNewsCategories() {
        List<String> dbCats = newsArticleRepository.findDistinctCategories();
        if (dbCats != null && !dbCats.isEmpty()) {
            return dbCats;
        }
        return List.of(
                "Xu hướng làm đẹp",
                "Chăm sóc da",
                "Hướng dẫn sử dụng",
                "Kiến thức làm đẹp"
        );
    }

    @Override
    public NewsDTO getFeaturedArticle() {
        Optional<iuh.wwwprogramming.entity.NewsArticle> featured = newsArticleRepository.findFirstByIsFeaturedTrueAndActiveTrue();
        if (featured.isPresent()) {
            return convertToDTO(featured.get());
        }
        List<iuh.wwwprogramming.entity.NewsArticle> all = newsArticleRepository.findAllByActiveTrueOrderByPublishedDateDesc();
        if (!all.isEmpty()) {
            return convertToDTO(all.get(0));
        }
        if (newsArticleRepository.count() == 0) {
            List<NewsDTO> raw = buildRawArticles();
            return raw.isEmpty() ? null : raw.get(0);
        }
        return null;
    }

    private NewsDTO convertToDTO(iuh.wwwprogramming.entity.NewsArticle entity) {
        if (entity == null) return null;

        List<String> linkedIds = new ArrayList<>();
        if (entity.getLinkedProductIds() != null && !entity.getLinkedProductIds().trim().isEmpty()) {
            linkedIds = Arrays.stream(entity.getLinkedProductIds().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        List<ProductCardDTO> products = findLinkedProducts(linkedIds);

        List<String> paragraphs = new ArrayList<>();
        if (entity.getContent() != null && !entity.getContent().trim().isEmpty()) {
            paragraphs = Arrays.stream(entity.getContent().split("\n\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        List<String> tags = new ArrayList<>();
        if (entity.getTags() != null && !entity.getTags().trim().isEmpty()) {
            tags = Arrays.stream(entity.getTags().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        return NewsDTO.builder()
                .id(entity.getId().intValue())
                .slug(entity.getSlug())
                .title(entity.getTitle())
                .excerpt(entity.getExcerpt())
                .content(entity.getContent())
                .paragraphs(paragraphs)
                .date(entity.getPublishedDate())
                .category(entity.getCategory())
                .author(entity.getAuthor())
                .authorRole(entity.getAuthorRole())
                .readTime(entity.getReadTime())
                .image(entity.getImage())
                .tags(tags)
                .viewsCount(entity.getViewsCount())
                .linkedProducts(products)
                .recommendationReasons(RECOMMENDATION_REASONS)
                .build();
    }

    private List<NewsDTO> fallbackGetAllNews(String category, String keyword) {
        List<NewsDTO> all = buildRawArticles();
        return all.stream()
                .filter(a -> {
                    if (category == null || category.trim().isEmpty() || "all".equalsIgnoreCase(category.trim())) {
                        return true;
                    }
                    return a.getCategory().equalsIgnoreCase(category.trim());
                })
                .filter(a -> {
                    if (keyword == null || keyword.trim().isEmpty()) {
                        return true;
                    }
                    String kw = keyword.trim().toLowerCase();
                    boolean inTitle = a.getTitle() != null && a.getTitle().toLowerCase().contains(kw);
                    boolean inExcerpt = a.getExcerpt() != null && a.getExcerpt().toLowerCase().contains(kw);
                    boolean inCategory = a.getCategory() != null && a.getCategory().toLowerCase().contains(kw);
                    boolean inAuthor = a.getAuthor() != null && a.getAuthor().toLowerCase().contains(kw);
                    boolean inTags = a.getTags() != null && a.getTags().stream().anyMatch(t -> t.toLowerCase().contains(kw));
                    return inTitle || inExcerpt || inCategory || inAuthor || inTags;
                })
                .collect(Collectors.toList());
    }

    private NewsDTO fallbackGetNewsBySlug(String slug) {
        List<NewsDTO> all = buildRawArticles();
        for (NewsDTO a : all) {
            if (slug.equalsIgnoreCase(a.getSlug()) || slug.equals(String.valueOf(a.getId()))) {
                return a;
            }
        }
        return null;
    }

    private List<NewsDTO> fallbackGetRelatedNews(Integer currentId, String category, int limit) {
        List<NewsDTO> all = buildRawArticles();
        return all.stream()
                .filter(a -> currentId == null || !a.getId().equals(currentId))
                .sorted((a, b) -> {
                    boolean aSame = category != null && category.equalsIgnoreCase(a.getCategory());
                    boolean bSame = category != null && category.equalsIgnoreCase(b.getCategory());
                    if (aSame && !bSame) return -1;
                    if (!aSame && bSame) return 1;
                    return Integer.compare(b.getId(), a.getId());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<NewsDTO> buildRawArticles() {
        Map<Integer, List<String>> newsProductMap = Map.of(
                1, List.of("pc-016", "pc-009"),
                2, List.of("pc-001", "pc-012"),
                3, List.of("pc-013", "pc-050"),
                4, List.of("pc-040", "pc-046"),
                5, List.of("pc-049", "pc-052")
        );

        List<NewsDTO> list = new ArrayList<>();

        // Article 1
        list.add(createArticle(
                1,
                "xu-huong-lam-dep-2026-thien-nhien-va-cham-soc-da",
                "Xu hướng làm đẹp 2026: Thiên nhiên và chăm sóc da",
                "Sản phẩm thiên nhiên đang trở thành xu hướng hàng đầu trong chăm sóc da mặt và cơ thể với độ an toàn và lành tính cao.",
                List.of(
                        "Năm 2026 đánh dấu sự lên ngôi mạnh mẽ của xu hướng làm đẹp bền vững, trong đó các sản phẩm có nguồn gốc thiên nhiên và quy trình chăm sóc da tối giản đang trở thành lựa chọn ưu tiên của nhiều người tiêu dùng. Không chỉ dừng lại ở yếu tố hiệu quả, người dùng hiện nay còn quan tâm đến độ an toàn, độ lành tính và sự thân thiện với môi trường trong từng sản phẩm.",
                        "Một trong những thay đổi nổi bật là việc người tiêu dùng có xu hướng lựa chọn các sản phẩm chứa chiết xuất thực vật như tràm trà, rau má, lô hội, hoa cúc và các loại dầu thiên nhiên. Những thành phần này không chỉ giúp làm dịu da mà còn hỗ trợ phục hồi và nuôi dưỡng làn da một cách bền vững hơn.",
                        "Bên cạnh đó, quy trình skincare nhiều bước đang dần được thay thế bằng phương pháp chăm sóc da thông minh và tinh gọn (Skinimalism). Thay vì sử dụng quá nhiều sản phẩm trong một lần dưỡng, xu hướng mới tập trung vào việc chọn đúng sản phẩm phù hợp với nhu cầu thực tế của làn da.",
                        "Ngoài ra, bao bì tối giản, có thể tái chế và các thương hiệu hướng đến phát triển xanh cũng đang chiếm được nhiều cảm tình hơn từ người tiêu dùng trẻ. Đây là tín hiệu cho thấy làm đẹp không còn chỉ là câu chuyện bên ngoài, mà còn gắn liền với phong cách sống và nhận thức bền vững.",
                        "Trong thời gian tới, xu hướng làm đẹp thiên nhiên được dự đoán sẽ tiếp tục phát triển mạnh, đặc biệt ở nhóm khách hàng yêu thích lối sống lành mạnh và ưu tiên sự cân bằng sinh học cho làn da."
                ),
                "12/04/2026",
                "Xu hướng làm đẹp",
                "PinkyCloud Editorial",
                "Ban Biên Tập Chuyên Môn",
                "6 phút đọc",
                "/IMG/news01.png",
                List.of("Skincare 2026", "Clean Beauty", "Rau Má", "Skinimalism"),
                2480L,
                newsProductMap.get(1)
        ));

        // Article 2
        list.add(createArticle(
                2,
                "bi-quyet-chon-my-pham-phu-hop-cho-da-nhay-cam",
                "Bí quyết chọn mỹ phẩm phù hợp cho da nhạy cảm",
                "Hướng dẫn cách đọc thành phần, tránh các hoạt chất dễ gây kích ứng và lựa chọn sản phẩm an toàn cho làn da nhạy cảm.",
                List.of(
                        "Da nhạy cảm là một trong những loại da cần được chăm sóc cẩn thận nhất vì rất dễ phản ứng với mỹ phẩm, thời tiết hoặc môi trường xung quanh. Việc lựa chọn mỹ phẩm phù hợp không chỉ giúp bảo vệ da mà còn hạn chế tình trạng kích ứng, mẩn đỏ và khô rát kéo dài.",
                        "Khi chọn mỹ phẩm cho da nhạy cảm, điều quan trọng đầu tiên là phải đọc kỹ bảng thành phần. Người dùng nên ưu tiên các sản phẩm không chứa cồn khô, hương liệu tổng hợp, paraben mạnh hoặc các chất tẩy rửa có độ làm sạch quá cao (Sulfates). Những thành phần này có thể khiến hàng rào bảo vệ da bị tổn thương và làm da trở nên yếu hơn.",
                        "Ngoài ra, nên ưu tiên các sản phẩm có thành phần phục hồi như Panthenol (B5), Ceramide, Allantoin, chiết xuất rau má (Centella Asiatica) hoặc Hyaluronic Acid. Đây là những hoạt chất lành tính, hỗ trợ cấp ẩm và củng cố hàng rào lipid bảo vệ da hiệu quả.",
                        "Một lưu ý quan trọng khác là luôn thử sản phẩm ở vùng da nhỏ (Patch test tại quai hàm) trong 24 - 48 giờ trước khi dùng cho toàn bộ khuôn mặt. Đây là bước cần thiết để kiểm tra phản ứng của da, đặc biệt với những làn da đang treatment hoặc mới peel.",
                        "Bên cạnh việc chọn sản phẩm, người có làn da nhạy cảm cũng nên xây dựng quy trình chăm sóc da tối giản, tránh kết hợp quá nhiều hoạt chất mạnh cùng một thời điểm. Sự kiên trì và lựa chọn đúng sản phẩm sẽ giúp làn da khỏe hơn rõ rệt theo thời gian."
                ),
                "05/04/2026",
                "Chăm sóc da",
                "Dr. Hoàng Lan",
                "Cố Vấn Da Liễu PinkyCloud",
                "5 phút đọc",
                "/IMG/news02.png",
                List.of("Da Nhạy Cảm", "Dịu Lành", "Chống Nắng", "Bảo Vệ Hàng Rào Da"),
                3120L,
                newsProductMap.get(2)
        ));

        // Article 3
        list.add(createArticle(
                3,
                "cach-su-dung-serum-dung-chuan-de-da-sang-khoe",
                "Cách sử dụng serum đúng chuẩn để da sáng khỏe",
                "Tìm hiểu quy trình dưỡng da hiệu quả với serum, toner và kem chống nắng để tối ưu hóa khả năng thẩm thấu dưỡng chất.",
                List.of(
                        "Serum là một trong những sản phẩm chăm sóc da được nhiều người yêu thích nhờ khả năng chứa nồng độ hoạt chất cao, thẩm thấu nhanh và tập trung giải quyết từng vấn đề cụ thể của làn da. Tuy nhiên, để serum phát huy hiệu quả tối đa, cách sử dụng đúng là điều tối quan trọng.",
                        "Trước tiên, làn da cần được làm sạch hoàn toàn bằng sữa rửa mặt dịu nhẹ. Sau đó, hãy dùng toner để cân bằng độ pH tự nhiên và tạo độ ẩm đệm cho serum hấp thụ sâu hơn. Khi thoa serum, chỉ cần lấy một lượng vừa đủ (thường từ 2 đến 3 giọt), rồi nhẹ nhàng vỗ đều lên da thay vì chà xát hay miết mạnh.",
                        "Nguyên tắc tiếp theo là chọn serum phù hợp với nhu cầu da: Nếu da khô mất nước, hãy ưu tiên serum cấp ẩm chứa Hyaluronic Acid đa phân tử hoặc Glycerin. Nếu da xỉn màu thâm mụn, có thể chọn Vitamin C hoặc Niacinamide. Nếu da có dấu hiệu lão hóa sớm, các serum chứa Peptide hoặc Retinol vi nang sẽ là lựa chọn phù hợp hơn.",
                        "Thời điểm sử dụng serum cũng ảnh hưởng lớn đến kết quả: Một số serum cấp ẩm có thể dùng cả sáng và tối, nhưng các hoạt chất mạnh như Retinol hay AHA/BHA thường chỉ nên dùng vào ban đêm. Vào ban ngày, sau bước serum và dưỡng ẩm mỏng, nhất định phải sử dụng kem chống nắng phổ rộng để bảo vệ da khỏi tác hại của tia UV.",
                        "Việc sử dụng serum đúng cách không chỉ giúp da hấp thụ dưỡng chất tốt hơn mà còn tối ưu hóa chi phí chăm sóc da, mang lại làn da căng bóng ngậm nước và đều màu rõ rệt."
                ),
                "28/03/2026",
                "Hướng dẫn sử dụng",
                "Ngọc Trâm",
                "Beauty Specialist",
                "4 phút đọc",
                "/IMG/news03.png",
                List.of("Serum", "Hyaluronic Acid", "Cấp Ẩm", "Skincare Routine"),
                1890L,
                newsProductMap.get(3)
        ));

        // Article 4
        list.add(createArticle(
                4,
                "cach-duong-da-ban-dem-de-phuc-hoi-lan-da",
                "Cách dưỡng da ban đêm để phục hồi làn da hiệu quả",
                "Ban đêm là thời điểm vàng để tái tạo tế bào, hãy tận dụng chu trình skincare ban đêm đúng cách để thức dậy với làn da rạng rỡ.",
                List.of(
                        "Ban đêm là khoảng thời gian các tế bào da bước vào quá trình tự phục hồi, sửa chữa tổn thương và tái tạo mạnh mẽ nhất gấp nhiều lần so với ban ngày. Vì vậy, việc xây dựng một chu trình dưỡng da ban đêm hợp lý sẽ giúp tối ưu hóa khả năng hấp thu dưỡng chất của làn da.",
                        "Bước đầu tiên luôn là làm sạch sâu hai bước (Double Cleansing) bằng dầu/nước tẩy trang và sữa rửa mặt dịu nhẹ. Thao tác này giúp loại bỏ triệt để bụi mịn, dầu thừa tích tụ và cặn kem chống nắng sau một ngày dài năng động.",
                        "Sau khi cân bằng da bằng toner cấp ẩm, serum và kem dưỡng đêm sẽ là hai nhân tố chủ chốt giúp cung cấp dưỡng chất chuyên sâu và khóa chặt màng ẩm. Vào ban đêm, bạn có thể bổ sung các hoạt chất tái tạo bề mặt như AHA-BHA-PHA hoặc mặt nạ ngủ để tăng cường dưỡng ẩm cho da.",
                        "Bên cạnh mỹ phẩm, giấc ngủ sâu từ 7 - 8 tiếng mỗi ngày và việc uống đủ nước cũng đóng vai trò then chốt giúp quá trình thanh lọc da diễn ra trơn tru nhất.",
                        "Một chu trình chăm sóc da ban đêm chuẩn xác sẽ giúp bạn thức dậy mỗi sáng với làn da mềm mịn, căng mướt và tràn đầy sức sống."
                ),
                "20/03/2026",
                "Chăm sóc da",
                "PinkyCloud Editorial",
                "Ban Biên Tập Chuyên Môn",
                "5 phút đọc",
                "/IMG/news04.png",
                List.of("Dưỡng Ban Đêm", "Phục Hồi", "Sleeping Mask", "Màng Ẩm"),
                2740L,
                newsProductMap.get(4)
        ));

        // Article 5
        list.add(createArticle(
                5,
                "top-thanh-phan-duong-da-nen-co-trong-my-pham",
                "Top những thành phần dưỡng da nên có trong mỹ phẩm",
                "Những hoạt chất vàng được khoa học chứng minh hiệu quả giúp cải thiện kết cấu và duy trì thanh xuân cho làn da.",
                List.of(
                        "Khi lựa chọn mỹ phẩm, việc thấu hiểu các thành phần hoạt tính (Active Ingredients) sẽ giúp bạn đưa ra quyết định sáng suốt và chuẩn xác nhất cho tình trạng da của mình mà không bị chi phối bởi quảng cáo hoa mỹ.",
                        "Thành phần đầu tiên không thể thiếu là Hyaluronic Acid (HA) - hoạt chất ngậm nước huyền thoại có khả năng giữ trọng lượng nước gấp 1000 lần phân tử của nó, giúp tế bào da luôn căng mọng và đàn hồi.",
                        "Niacinamide (Vitamin B3) là thành phần đa nhiệm được ưa chuộng bậc nhất hiện nay nhờ công dụng điều tiết dầu thừa, thu nhỏ lỗ chân lông, làm đều màu da và hỗ trợ tăng sinh collagen tự nhiên.",
                        "Vitamin C tinh khiết (L-Ascorbic Acid) mang lại hiệu quả chống oxy hóa vượt trội, vô hiệu hóa các gốc tự do gây hại và làm mờ các đốm thâm sạm nám một cách bền vững.",
                        "Trong khi đó, Ceramide và Panthenol đóng vai trò như 'xi măng sinh học' gắn kết các tế bào sừng, vá lành các vết rạn nứt trên hàng rào bảo vệ da do ô nhiễm hoặc do treatment quá đà.",
                        "Nắm vững các thành phần này sẽ giúp bạn dễ dàng tự xây dựng một routine chăm sóc da khoa học, tối ưu chi phí và đạt hiệu quả lâu dài."
                ),
                "15/03/2026",
                "Kiến thức làm đẹp",
                "Dr. Hoàng Lan",
                "Cố Vấn Da Liễu PinkyCloud",
                "6 phút đọc",
                "/IMG/news05.png",
                List.of("Thành Phần Vàng", "Niacinamide", "Vitamin C", "Ceramide"),
                4190L,
                newsProductMap.get(5)
        ));

        return list;
    }

    private NewsDTO createArticle(
            int id,
            String slug,
            String title,
            String excerpt,
            List<String> paragraphs,
            String date,
            String category,
            String author,
            String authorRole,
            String readTime,
            String image,
            List<String> tags,
            long views,
            List<String> linkedProductIds
    ) {
        String fullContent = String.join("\n\n", paragraphs);

        Map<String, String> recMap = new HashMap<>();
        if (linkedProductIds != null) {
            for (String pid : linkedProductIds) {
                if (RECOMMENDATION_REASONS.containsKey(pid)) {
                    recMap.put(pid, RECOMMENDATION_REASONS.get(pid));
                }
            }
        }

        return NewsDTO.builder()
                .id(id)
                .slug(slug)
                .title(title)
                .excerpt(excerpt)
                .content(fullContent)
                .paragraphs(paragraphs)
                .date(date)
                .category(category)
                .author(author)
                .authorRole(authorRole)
                .readTime(readTime)
                .image(image)
                .tags(tags)
                .viewsCount(views)
                .recommendationReasons(recMap)
                .linkedProducts(findLinkedProducts(linkedProductIds))
                .build();
    }

    private List<ProductCardDTO> findLinkedProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) {
            return List.of();
        }

        return products.stream().map(p -> {
            int discount = (p.getDiscount() != null) ? p.getDiscount() : 0;
            Double price = (p.getPrice() != null) ? p.getPrice().doubleValue() : 0.0;
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
