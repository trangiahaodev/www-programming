-- =============================================================================
-- Initial Data for MariaDB (Single Source of Truth - SSOT)
-- Project: PinkyCloud Cosmetic Shop
-- Architectural Rule: Dual-Key Strategy (Surrogate Key UUID 36 chars + Business Key)
-- Character Set: utf8mb4 / utf8mb4_unicode_ci
-- =============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------------------------
-- 1. Table: roles
-- -----------------------------------------------------------------------------
INSERT INTO `roles` (`name`, `description`) VALUES
('ROLE_ADMIN', 'Quản trị viên hệ thống có toàn quyền điều hành và quản lý danh mục'),
('ROLE_CUSTOMER', 'Khách hàng thành viên mua sắm và theo dõi đơn hàng')
ON DUPLICATE KEY UPDATE `description` = VALUES(`description`);

-- -----------------------------------------------------------------------------
-- 2. Table: users
-- Surrogate Key (id): Chuẩn UUID 36 ký tự tiền tố 00000000-...
-- Business Key (user_code): AD26000001, KH26000001, KH26000002
-- Passwords:
--   admin@pinkycloud.com -> admin123 (BCrypt: $2a$10$NlCHmU5XfkNLxL/fKlj8Ce9ifjIpcJbcZXbZb2pNFmqJMW6l9BP9a)
--   khachhang@pinkycloud.com / lan.nguyen@pinkycloud.com -> 123456 (BCrypt: $2a$10$KXpnkqsZkHOz33zHWqdWyOnP30fAhAMFjDOi9dpDkDC8eMaYiw5La)
-- -----------------------------------------------------------------------------
INSERT INTO `users` (`id`, `user_code`, `email`, `password`, `full_name`, `phone`, `address`, `role`, `active`, `is_deleted`, `created_at`, `updated_at`) VALUES
('00000000-0000-0000-0000-000000000001', 'AD26000001', 'admin@pinkycloud.com', '$2a$10$NlCHmU5XfkNLxL/fKlj8Ce9ifjIpcJbcZXbZb2pNFmqJMW6l9BP9a', 'Quản Trị Viên Pinky', '0988888888', 'Hệ Thống Pinky Cloud', 'ROLE_ADMIN', TRUE, FALSE, NOW(6), NOW(6)),
('00000000-0000-0000-0000-000000000002', 'KH26000001', 'khachhang@pinkycloud.com', '$2a$10$KXpnkqsZkHOz33zHWqdWyOnP30fAhAMFjDOi9dpDkDC8eMaYiw5La', 'Nguyễn Thu Hà', '0912345678', '12 Nguyễn Văn Bảo, Phường 4, Quận Gò Vấp, TP. Hồ Chí Minh', 'ROLE_CUSTOMER', TRUE, FALSE, NOW(6), NOW(6)),
('00000000-0000-0000-0000-000000000003', 'KH26000002', 'lan.nguyen@pinkycloud.com', '$2a$10$KXpnkqsZkHOz33zHWqdWyOnP30fAhAMFjDOi9dpDkDC8eMaYiw5La', 'Nguyễn Thị Lan', '0987654321', '12 Nguyễn Văn Bảo, Phường 4, Quận Gò Vấp, TP. Hồ Chí Minh', 'ROLE_CUSTOMER', TRUE, FALSE, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE `full_name` = VALUES(`full_name`), `role` = VALUES(`role`), `active` = VALUES(`active`), `is_deleted` = VALUES(`is_deleted`);

-- -----------------------------------------------------------------------------
-- 3. Table: categories
-- Surrogate Key (id): Chuẩn UUID 36 ký tự tiền tố 10000000-...
-- Business Key (category_code): CAT001 -> CAT006
-- -----------------------------------------------------------------------------
INSERT INTO `categories` (`id`, `category_code`, `name`, `description`, `active`, `is_deleted`, `created_at`, `updated_at`) VALUES
('10000000-0000-0000-0000-000000000001', 'CAT001', 'Chăm sóc da', 'Các sản phẩm chăm sóc da chuyên sâu, serum phục hồi, toner cân bằng và kem đặc trị', TRUE, FALSE, NOW(6), NOW(6)),
('10000000-0000-0000-0000-000000000002', 'CAT002', 'Chống nắng', 'Kem chống nắng, sữa chống nắng quang phổ rộng bảo vệ da trước tia UVA/UVB', TRUE, FALSE, NOW(6), NOW(6)),
('10000000-0000-0000-0000-000000000003', 'CAT003', 'Trang điểm', 'Son môi lì, kem nền kiềm dầu, phấn phủ và mascara trang điểm cao cấp', TRUE, FALSE, NOW(6), NOW(6)),
('10000000-0000-0000-0000-000000000004', 'CAT004', 'Làm sạch da', 'Sữa rửa mặt dịu nhẹ, nước tẩy trang lành tính cho mọi loại da nhạy cảm', TRUE, FALSE, NOW(6), NOW(6)),
('10000000-0000-0000-0000-000000000005', 'CAT005', 'Dưỡng ẩm', 'Kem dưỡng ẩm, cấp nước chuyên sâu và củng cố hàng rào bảo vệ màng ẩm da', TRUE, FALSE, NOW(6), NOW(6)),
('10000000-0000-0000-0000-000000000006', 'CAT006', 'Chăm sóc môi', 'Son dưỡng môi, sáp dưỡng ẩm và tẩy tế bào chết môi cho đôi môi căng mọng', TRUE, FALSE, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `description` = VALUES(`description`), `active` = VALUES(`active`), `is_deleted` = VALUES(`is_deleted`);

-- -----------------------------------------------------------------------------
-- 4. Table: products (Đầy đủ 6 sản phẩm chủ lực kịch bản AI test + sản phẩm hỗ trợ)
-- Surrogate Key (id): Chuẩn UUID 36 ký tự tiền tố 20000000-...
-- Business Key (product_code): SP26000001 -> SP26000010
-- Foreign Key (category_id): Trỏ chính xác tới categories(id) chuẩn UUID 36 ký tự
-- -----------------------------------------------------------------------------
INSERT INTO `products` (`id`, `product_code`, `name`, `brand`, `price`, `discount`, `image_url`, `stock_quantity`, `currency`, `origin`, `description`, `ingredients`, `usage_instructions`, `barcode`, `is_active`, `is_deleted`, `is_hot`, `is_new`, `rating`, `review_count`, `sold_count`, `category_id`, `created_at`, `updated_at`) VALUES
('20000000-0000-0000-0000-000000000001', 'SP26000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'Laneige', 450000.00, 15, '/IMG/anh11.png', 80, 'VND', 'Hàn Quốc', 
'Kem dưỡng ẩm sâu thế hệ mới với Blue Hyaluronic Acid siêu phân tử giúp phục hồi hàng rào độ ẩm của làn da thêm mịn mượt và rạng rỡ.',
'Water, Butylene Glycol, Glycerin, Squalane, Sucrose Polystearate, Pentaerythrityl Tetraethylhexanoate, Methyl Trimethicone, Hydrolyzed Hyaluronic Acid, Ceramide NP, Beta-Glucan, Tocopherol.',
'Thoa đều một lượng kem vừa phải lên toàn bộ khuôn mặt theo chiều cấu tạo da sau bước serum sáng và tối.', '8809643063214', TRUE, FALSE, TRUE, FALSE, 4.9, 215, 512, '10000000-0000-0000-0000-000000000005', NOW(6), NOW(6)),

('20000000-0000-0000-0000-000000000002', 'SP26000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'Cetaphil', 200000.00, 10, '/IMG/anh2.png', 150, 'VND', 'Canada',
'Sữa rửa mặt công thức dịu lành không xà phòng, củng cố hàng rào bảo vệ tự nhiên của da nhạy cảm và duy trì độ ẩm tự nhiên suốt cả ngày.',
'Water, Cetyl Alcohol, Propylene Glycol, Sodium Lauryl Sulfate, Stearyl Alcohol, Methylparaben, Propylparaben, Butylparaben.',
'Làm ướt da mặt, lấy một lượng vừa đủ massage nhẹ nhàng rồi rửa sạch lại với nước hoặc dùng bông mềm lau khô.', '3499320008320', TRUE, FALSE, FALSE, FALSE, 4.7, 180, 670, '10000000-0000-0000-0000-000000000004', NOW(6), NOW(6)),

('20000000-0000-0000-0000-000000000003', 'SP26000003', 'Son dưỡng môi DHC Lip Cream', 'DHC', 160000.00, 5, '/IMG/anh3.png', 100, 'VND', 'Nhật Bản',
'Son dưỡng môi không màu chiết xuất từ dầu olive nguyên chất, lá lô hội và cam thảo giúp dưỡng ẩm sâu, ngăn ngừa nứt nẻ và làm hồng môi.',
'Lanolin Oil, Caprylic/Capric Triglyceride, Euphorbia Cerifera Wax, Olea Europaea (Olive) Fruit Oil, Squalane, Stearic Acid, Tocopherol.',
'Thoa đều lên môi bất cứ khi nào cảm thấy môi khô ráp hoặc dùng làm lớp lót trước khi thoa son màu.', '4511413302164', TRUE, FALSE, FALSE, FALSE, 4.8, 142, 430, '10000000-0000-0000-0000-000000000006', NOW(6), NOW(6)),

('20000000-0000-0000-0000-000000000004', 'SP26000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'La Roche-Posay', 725000.00, 12, '/IMG/anh7.png', 75, 'VND', 'Pháp',
'Serum chứa phức hợp 2 loại Hyaluronic Acid nguyên chất, Vitamin B5 và Madecassoside giúp làm dịu, phục hồi màng bảo vệ da và làm đầy nếp nhăn.',
'Aqua / Water, Glycerin, Alcohol Denat., Propylene Glycol, Panthenol, Pentylene Glycol, Dimethicone, Madecassoside, Sodium Hyaluronate, Hydrolyzed Hyaluronic Acid, Tocopherol.',
'Nhỏ 3-4 giọt tinh chất lên trán và 2 má, vỗ nhẹ đều khắp mặt trước bước kem dưỡng vào buổi sáng và buổi tối.', '3337875583626', TRUE, FALSE, TRUE, TRUE, 4.9, 290, 780, '10000000-0000-0000-0000-000000000001', NOW(6), NOW(6)),

('20000000-0000-0000-0000-000000000005', 'SP26000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'Anessa', 590000.00, 15, '/IMG/anh1.png', 120, 'VND', 'Nhật Bản',
'Sữa chống nắng kiềm dầu bảo vệ phổ rộng với công nghệ Auto Booster giúp màng lọc UV trở nên vững chắc hơn khi tiếp xúc với nước, mồ hôi và nhiệt độ cao.',
'Dimethicone, Water, Zinc Oxide, Alcohol, Talc, Isododecane, Diisopropyl Sebacate, Octocrylene, Ethylhexyl Salicylate, Silica, Titanium Dioxide, Camellia Sinensis Leaf Extract, Sodium Hyaluronate, Soluble Collagen.',
'Lắc đều trước khi sử dụng. Thoa một lượng vừa đủ lên vùng mặt và cổ trước khi ra ngoài nắng 15-20 phút. Thoa lại sau 2-3 giờ nếu hoạt động nhiều ngoài trời.', '4909978120757', TRUE, FALSE, TRUE, TRUE, 5.0, 340, 890, '10000000-0000-0000-0000-000000000002', NOW(6), NOW(6)),

('20000000-0000-0000-0000-000000000006', 'SP26000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'Bioderma', 210000.00, 0, '/IMG/anh4.png', 90, 'VND', 'Pháp',
'Nước tẩy trang công nghệ hạt micelle làm sạch sâu cặn trang điểm và bụi mịn mà không gây khô rát cho làn da nhạy cảm.',
'Aqua/Water/Eau, PEG-6 Caprylic/Capric Glycerides, Fructooligosaccharides, Mannitol, Xylitol, Rhamnose, Cucumis Sativus (Cucumber) Fruit Extract, Propylene Glycol, Cetrimonium Bromide, Disodium EDTA.',
'Thấm ướt bông tẩy trang với dung dịch rồi nhẹ nhàng lau sạch vùng mặt, mắt và môi. Không cần rửa lại với nước.', '3401575645851', TRUE, FALSE, FALSE, FALSE, 4.8, 160, 480, '10000000-0000-0000-0000-000000000004', NOW(6), NOW(6)),

('20000000-0000-0000-0000-000000000007', 'SP26000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'Black Rouge', 160000.00, 20, '/IMG/anh27.png', 110, 'VND', 'Hàn Quốc',
'Son kem lì chất velvet xốp mịn lướt nhẹ trên môi, sắc son nâu gạch trầm ấm tôn da tuyệt đối, giữ màu chuẩn đẹp không gây khô môi.',
'Dimethicone, Dimethicone Crosspolymer, Polyglyceryl-2 Triisostearate, Iron Oxides (CI 77491), Simmondsia Chinensis Seed Oil, Butyrospermum Parkii Butter, Tocopheryl Acetate.',
'Gạt bớt son thừa ở miệng cọ, thoa một lớp mỏng từ lòng môi rồi tán nhẹ lan ra viền môi để tạo hiệu ứng tự nhiên rạng rỡ.', '8809564281315', TRUE, FALSE, TRUE, FALSE, 4.9, 420, 1200, '10000000-0000-0000-0000-000000000003', NOW(6), NOW(6)),

('20000000-0000-0000-0000-000000000008', 'SP26000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 'Maybelline', 245000.00, 10, '/IMG/anh32.png', 95, 'VND', 'Mỹ',
'Kem nền kiềm dầu che phủ lỗ chân lông hoàn hảo, hạt phấn micro-powders giúp lớp nền mịn lì tự nhiên suốt 16 giờ.',
'Aqua / Water, Cyclohexasiloxane, Nylon-12, Isododecane, Alcohol Denat., Cyclopentasiloxane, PEG-10 Dimethicone, Cetyl PEG/PPG-10/1 Dimethicone, Titanium Dioxide, Magnesium Sulfate, Tocopherol.',
'Chấm kem nền lên trán, mũi, hai má và cằm. Dùng cọ hoặc mút trang điểm ẩm dặm nhẹ nhàng từ trong ra ngoài.', '041554433418', TRUE, FALSE, TRUE, TRUE, 4.8, 128, 340, '10000000-0000-0000-0000-000000000003', NOW(6), NOW(6)),

('20000000-0000-0000-0000-000000000009', 'SP26000009', 'Kem Chống Nắng Vichy Thoáng Nhẹ Không Bóng Dầu SPF 50 50ml', 'Vichy', 348000.00, 10, '/IMG/anh5.png', 65, 'VND', 'Pháp',
'Kem chống nắng dạng kem gel mỏng nhẹ bảo vệ da toàn diện với màng lọc Mexoryl độc quyền, kiểm soát dầu thừa hiệu quả.',
'Aqua / Water, Homosalate, Silica, Ethylhexyl Salicylate, Ethylhexyl Triazone, C12-15 Alkyl Benzoate, Bis-Ethylhexyloxyphenol Methoxyphenyl Triazine, Drometrizole Trisiloxane, Butyl Methoxydibenzoylmethane.',
'Thoa kem chống nắng mỗi sáng trước khi tiếp xúc ánh nắng mặt trời. Thoa lại khi cần thiết.', '3337871322649', TRUE, FALSE, FALSE, FALSE, 4.7, 95, 230, '10000000-0000-0000-0000-000000000002', NOW(6), NOW(6)),

('20000000-0000-0000-0000-000000000010', 'SP26000010', 'Tinh Chất Rau Má Skin1004 Madagascar Centella Ampoule 100ml', 'Skin1004', 335000.00, 15, '/IMG/anh6.png', 85, 'VND', 'Hàn Quốc',
'Chiết xuất 100% từ rau má Madagascar nguyên chất giúp làm dịu da nhạy cảm, giảm kích ứng, kháng viêm và củng cố hàng rào da.',
'Centella Asiatica Extract (100%).',
'Sau bước toner, nhỏ 2-3 giọt tinh chất thoa đều lên toàn bộ khuôn mặt và vỗ nhẹ cho dưỡng chất thẩm thấu.', '8809576260056', TRUE, FALSE, TRUE, FALSE, 4.9, 310, 850, '10000000-0000-0000-0000-000000000001', NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `price` = VALUES(`price`), `stock_quantity` = VALUES(`stock_quantity`), `is_active` = VALUES(`is_active`), `is_deleted` = VALUES(`is_deleted`);

-- -----------------------------------------------------------------------------
-- 5. Table: orders (Đơn hàng mẫu đồng bộ với các kịch bản báo cáo doanh thu)
-- Surrogate Key (id): Chuẩn UUID 36 ký tự tiền tố 30000000-...
-- Business Key (order_code): ORD26000001 -> ORD26000050
-- Foreign Key (user_id): Trỏ chính xác tới users(id) chuẩn UUID 36 ký tự (hoặc NULL)
-- -----------------------------------------------------------------------------
INSERT INTO `orders` (`id`, `order_code`, `user_id`, `customer_name`, `customer_phone`, `shipping_address`, `note`, `total_amount`, `payment_method`, `payment_status`, `status`, `is_deleted`, `created_at`, `updated_at`) VALUES
('30000000-0000-0000-0000-000000000001', 'ORD26000001', '00000000-0000-0000-0000-000000000003', 'Nguyễn Thị Lan', '0987654321', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Giao giờ hành chính, gọi trước khi đến', 850000.00, 'COD', 'UNPAID', 'PENDING', FALSE, NOW(6), NOW(6)),
('30000000-0000-0000-0000-000000000002', 'ORD26000002', '00000000-0000-0000-0000-000000000002', 'Trần Văn An', '0912345678', '45 Lê Lợi, Q.1, TP.HCM', 'Đóng gói hộp quà cẩn thận', 320000.00, 'VNPAY', 'PAID', 'PROCESSING', FALSE, NOW(6), NOW(6)),
('30000000-0000-0000-0000-000000000003', 'ORD26000003', NULL, 'Lê Hoàng Mai', '0933221100', '78 Trần Phú, Hà Đông, Hà Nội', 'Giao cho lễ tân tòa nhà', 1450000.00, 'BANKING', 'PAID', 'SHIPPED', FALSE, NOW(6), NOW(6)),
('30000000-0000-0000-0000-000000000004', 'ORD26000004', NULL, 'Phạm Minh Tuấn', '0908776655', '102 Hai Bà Trưng, Đà Nẵng', 'Giao vào buổi sáng', 590000.00, 'COD', 'PAID', 'DELIVERED', FALSE, NOW(6), NOW(6)),
('30000000-0000-0000-0000-000000000005', 'ORD26000005', NULL, 'Đỗ Thu Hà', '0971122334', '15 Quang Trung, Cần Thơ', 'Khách đổi ý do bận công tác xa', 210000.00, 'COD', 'UNPAID', 'CANCELLED', FALSE, NOW(6), NOW(6)),
('30000000-0000-0000-0000-000000000006', 'ORD26000006', '00000000-0000-0000-0000-000000000002', 'Nguyễn Thu Hà', '0912345678', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Giao cuối tuần, gọi trước 15 phút', 565000.00, 'COD', 'UNPAID', 'PENDING', FALSE, DATE_SUB(NOW(6), INTERVAL 1 HOUR), DATE_SUB(NOW(6), INTERVAL 1 HOUR)),
('30000000-0000-0000-0000-000000000007', 'ORD26000007', '00000000-0000-0000-0000-000000000003', 'Nguyễn Thị Lan', '0987654321', '88 Pasteur, P.Bến Nghé, Q.1, TP.HCM', 'Gói chống sốc kỹ sản phẩm chai thủy tinh', 1018000.00, 'VNPAY', 'PAID', 'PROCESSING', FALSE, DATE_SUB(NOW(6), INTERVAL 4 HOUR), DATE_SUB(NOW(6), INTERVAL 3 HOUR)),
('30000000-0000-0000-0000-000000000008', 'ORD26000008', NULL, 'Vũ Minh Anh', '0903456789', '234 Xã Đàn, Q.Đống Đa, Hà Nội', 'Khách đặt làm quà sinh nhật', 1060000.00, 'BANKING', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 1 DAY), DATE_SUB(NOW(6), INTERVAL 12 HOUR)),
('30000000-0000-0000-0000-000000000009', 'ORD26000009', NULL, 'Hoàng Bảo Ngọc', '0918223344', '56 Nguyễn Văn Linh, Q.7, TP.HCM', NULL, 1525000.00, 'VNPAY', 'PAID', 'SHIPPED', FALSE, DATE_SUB(NOW(6), INTERVAL 2 DAY), DATE_SUB(NOW(6), INTERVAL 1 DAY)),
('30000000-0000-0000-0000-000000000010', 'ORD26000010', NULL, 'Đặng Quốc Huy', '0938112233', '19 Phan Chu Trinh, Q.Hải Châu, Đà Nẵng', 'Giao sau 17h30', 600000.00, 'COD', 'UNPAID', 'PENDING', FALSE, DATE_SUB(NOW(6), INTERVAL 2 DAY), DATE_SUB(NOW(6), INTERVAL 2 DAY)),
('30000000-0000-0000-0000-000000000011', 'ORD26000011', '00000000-0000-0000-0000-000000000002', 'Nguyễn Thu Hà', '0912345678', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Kiểm tra kỹ hạn sử dụng trước khi giao', 1340000.00, 'COD', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 3 DAY), DATE_SUB(NOW(6), INTERVAL 2 DAY)),
('30000000-0000-0000-0000-000000000012', 'ORD26000012', NULL, 'Bùi Thanh Thảo', '0977889900', '112 Cầu Giấy, Q.Cầu Giấy, Hà Nội', 'Khách yêu cầu hủy để đổi sang combo khác', 490000.00, 'VNPAY', 'UNPAID', 'CANCELLED', FALSE, DATE_SUB(NOW(6), INTERVAL 4 DAY), DATE_SUB(NOW(6), INTERVAL 3 DAY)),
('30000000-0000-0000-0000-000000000013', 'ORD26000013', '00000000-0000-0000-0000-000000000003', 'Nguyễn Thị Lan', '0987654321', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Gọi số bàn lễ tân nếu không nghe máy', 1116000.00, 'BANKING', 'PAID', 'PROCESSING', FALSE, DATE_SUB(NOW(6), INTERVAL 5 DAY), DATE_SUB(NOW(6), INTERVAL 4 DAY)),
('30000000-0000-0000-0000-000000000014', 'ORD26000014', NULL, 'Trương Gia Hân', '0965432109', '30 Nguyễn Huệ, Q.1, TP.HCM', 'Đóng gói kèm túi giấy PinkyCloud', 2235000.00, 'VNPAY', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 6 DAY), DATE_SUB(NOW(6), INTERVAL 5 DAY)),
('30000000-0000-0000-0000-000000000015', 'ORD26000015', NULL, 'Lý Nhật Nam', '0944556677', '77 Hùng Vương, TP.Huế', 'Mua tặng bạn gái', 800000.00, 'COD', 'UNPAID', 'SHIPPED', FALSE, DATE_SUB(NOW(6), INTERVAL 7 DAY), DATE_SUB(NOW(6), INTERVAL 6 DAY)),
('30000000-0000-0000-0000-000000000016', 'ORD26000016', NULL, 'Ngô Thanh Vân', '0911223355', '145 Võ Văn Tần, Q.3, TP.HCM', NULL, 1000000.00, 'BANKING', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 9 DAY), DATE_SUB(NOW(6), INTERVAL 7 DAY)),
('30000000-0000-0000-0000-000000000017', 'ORD26000017', NULL, 'Phan Hải Đăng', '0909887766', '22 Lạch Tray, Q.Ngô Quyền, Hải Phòng', 'Giao nhanh trong tuần', 670000.00, 'COD', 'UNPAID', 'PENDING', FALSE, DATE_SUB(NOW(6), INTERVAL 10 DAY), DATE_SUB(NOW(6), INTERVAL 10 DAY)),
('30000000-0000-0000-0000-000000000018', 'ORD26000018', '00000000-0000-0000-0000-000000000002', 'Nguyễn Thu Hà', '0912345678', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Đơn đặt nhầm địa chỉ cũ', 450000.00, 'COD', 'UNPAID', 'CANCELLED', FALSE, DATE_SUB(NOW(6), INTERVAL 12 DAY), DATE_SUB(NOW(6), INTERVAL 11 DAY)),
('30000000-0000-0000-0000-000000000019', 'ORD26000019', NULL, 'Đinh Thị Mỹ Linh', '0981234567', '09 Trần Hưng Đạo, Q.Hoàn Kiếm, Hà Nội', 'Khách quen, miễn phí thiệp viết tay', 913000.00, 'VNPAY', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 15 DAY), DATE_SUB(NOW(6), INTERVAL 13 DAY)),
('30000000-0000-0000-0000-000000000020', 'ORD26000020', '00000000-0000-0000-0000-000000000003', 'Nguyễn Thị Lan', '0987654321', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Giao trong giờ hành chính', 1375000.00, 'BANKING', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 20 DAY), DATE_SUB(NOW(6), INTERVAL 18 DAY)),
('30000000-0000-0000-0000-000000000021', 'ORD26000021', '00000000-0000-0000-0000-000000000002', 'Nguyễn Thu Hà', '0912345678', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Giao giờ hành chính', 790000.00, 'COD', 'UNPAID', 'PENDING', FALSE, DATE_SUB(NOW(6), INTERVAL 15 MINUTE), DATE_SUB(NOW(6), INTERVAL 15 MINUTE)),
('30000000-0000-0000-0000-000000000022', 'ORD26000022', NULL, 'Lê Minh Khôi', '0901234501', '54 Lý Tự Trọng, Q.1, TP.HCM', 'Gọi trước khi giao', 1060000.00, 'VNPAY', 'PAID', 'PROCESSING', FALSE, DATE_SUB(NOW(6), INTERVAL 2 HOUR), DATE_SUB(NOW(6), INTERVAL 1 HOUR)),
('30000000-0000-0000-0000-000000000023', 'ORD26000023', NULL, 'Phạm Thị Yến Nhi', '0902345602', '18 Nguyễn Chí Thanh, Q.Đống Đa, Hà Nội', NULL, 870000.00, 'BANKING', 'PAID', 'SHIPPED', FALSE, DATE_SUB(NOW(6), INTERVAL 6 HOUR), DATE_SUB(NOW(6), INTERVAL 3 HOUR)),
('30000000-0000-0000-0000-000000000024', 'ORD26000024', '00000000-0000-0000-0000-000000000003', 'Nguyễn Thị Lan', '0987654321', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Đóng gói hộp quà tặng', 480000.00, 'VNPAY', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 8 DAY), DATE_SUB(NOW(6), INTERVAL 6 DAY)),
('30000000-0000-0000-0000-000000000025', 'ORD26000025', NULL, 'Trần Quốc Bảo', '0913456703', '210 Cách Mạng Tháng 8, Q.3, TP.HCM', 'Giao buổi tối sau 18h', 696000.00, 'COD', 'UNPAID', 'PENDING', FALSE, DATE_SUB(NOW(6), INTERVAL 11 DAY), DATE_SUB(NOW(6), INTERVAL 11 DAY)),
('30000000-0000-0000-0000-000000000026', 'ORD26000026', NULL, 'Võ Thảo Vy', '0924567804', '67 Điện Biên Phủ, Q.Bình Thạnh, TP.HCM', 'Khách hẹn nhận tại sảnh chung cư', 650000.00, 'COD', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 13 DAY), DATE_SUB(NOW(6), INTERVAL 11 DAY)),
('30000000-0000-0000-0000-000000000027', 'ORD26000027', NULL, 'Nguyễn Hoàng Phúc', '0935678905', '105 Lê Duẩn, Q.Hải Châu, Đà Nẵng', 'Không giao vào chủ nhật', 800000.00, 'COD', 'UNPAID', 'PROCESSING', FALSE, DATE_SUB(NOW(6), INTERVAL 14 DAY), DATE_SUB(NOW(6), INTERVAL 13 DAY)),
('30000000-0000-0000-0000-000000000028', 'ORD26000028', NULL, 'Đào Phương Linh', '0946789006', '33 Phố Huế, Q.Hai Bà Trưng, Hà Nội', 'Đặt lại lần 2', 1450000.00, 'BANKING', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 16 DAY), DATE_SUB(NOW(6), INTERVAL 14 DAY)),
('30000000-0000-0000-0000-000000000029', 'ORD26000029', NULL, 'Hồ Gia Bảo', '0957890107', '412 Cộng Hòa, Q.Tân Bình, TP.HCM', 'Khách đổi sang thanh toán trực tiếp tại showroom', 400000.00, 'COD', 'UNPAID', 'CANCELLED', FALSE, DATE_SUB(NOW(6), INTERVAL 17 DAY), DATE_SUB(NOW(6), INTERVAL 16 DAY)),
('30000000-0000-0000-0000-000000000030', 'ORD26000030', '00000000-0000-0000-0000-000000000002', 'Nguyễn Thu Hà', '0912345678', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', NULL, 1120000.00, 'VNPAY', 'PAID', 'SHIPPED', FALSE, DATE_SUB(NOW(6), INTERVAL 18 DAY), DATE_SUB(NOW(6), INTERVAL 17 DAY)),
('30000000-0000-0000-0000-000000000031', 'ORD26000031', NULL, 'Lâm Ngọc Hân', '0968901208', '89 Nguyễn Trãi, Q.Thanh Xuân, Hà Nội', 'Kiểm hàng trước khi thanh toán', 640000.00, 'COD', 'UNPAID', 'PENDING', FALSE, DATE_SUB(NOW(6), INTERVAL 19 DAY), DATE_SUB(NOW(6), INTERVAL 19 DAY)),
('30000000-0000-0000-0000-000000000032', 'ORD26000032', NULL, 'Bùi Đức Anh', '0979012309', '150 Phan Xích Long, Q.Phú Nhuận, TP.HCM', 'Giao hỏa tốc nội thành', 1528000.00, 'VNPAY', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 21 DAY), DATE_SUB(NOW(6), INTERVAL 20 DAY)),
('30000000-0000-0000-0000-000000000033', 'ORD26000033', '00000000-0000-0000-0000-000000000003', 'Nguyễn Thị Lan', '0987654321', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Gửi bảo vệ nếu vắng nhà', 620000.00, 'COD', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 22 DAY), DATE_SUB(NOW(6), INTERVAL 20 DAY)),
('30000000-0000-0000-0000-000000000034', 'ORD26000034', NULL, 'Tạ Minh Thư', '0981112210', '25 Lê Lợi, TP.Vinh, Nghệ An', NULL, 695000.00, 'BANKING', 'PAID', 'PROCESSING', FALSE, DATE_SUB(NOW(6), INTERVAL 23 DAY), DATE_SUB(NOW(6), INTERVAL 22 DAY)),
('30000000-0000-0000-0000-000000000035', 'ORD26000035', NULL, 'Đoàn Khánh Linh', '0905123411', '72 Trần Phú, TP.Nha Trang, Khánh Hòa', 'Đóng gói chống sốc', 1045000.00, 'COD', 'UNPAID', 'SHIPPED', FALSE, DATE_SUB(NOW(6), INTERVAL 24 DAY), DATE_SUB(NOW(6), INTERVAL 23 DAY)),
('30000000-0000-0000-0000-000000000036', 'ORD26000036', NULL, 'Mai Anh Tuấn', '0916234512', '08 Lý Thường Kiệt, Q.Hoàn Kiếm, Hà Nội', 'Khách đi công tác đột xuất không nhận được hàng', 335000.00, 'VNPAY', 'UNPAID', 'CANCELLED', FALSE, DATE_SUB(NOW(6), INTERVAL 25 DAY), DATE_SUB(NOW(6), INTERVAL 24 DAY)),
('30000000-0000-0000-0000-000000000037', 'ORD26000037', '00000000-0000-0000-0000-000000000002', 'Nguyễn Thu Hà', '0912345678', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', NULL, 1155000.00, 'BANKING', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 26 DAY), DATE_SUB(NOW(6), INTERVAL 24 DAY)),
('30000000-0000-0000-0000-000000000038', 'ORD26000038', NULL, 'Cao Thùy Trang', '0937345613', '91 Võ Thị Sáu, TP.Biên Hòa, Đồng Nai', 'Giao giờ hành chính', 558000.00, 'COD', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 27 DAY), DATE_SUB(NOW(6), INTERVAL 25 DAY)),
('30000000-0000-0000-0000-000000000039', 'ORD26000039', NULL, 'Dương Văn Hùng', '0948456714', '14 Nguyễn Văn Cừ, Q.5, TP.HCM', 'Gọi trước 30 phút', 900000.00, 'COD', 'UNPAID', 'PENDING', FALSE, DATE_SUB(NOW(6), INTERVAL 28 DAY), DATE_SUB(NOW(6), INTERVAL 28 DAY)),
('30000000-0000-0000-0000-000000000040', 'ORD26000040', NULL, 'Trịnh Ngọc Mai', '0959567815', '205 Hoàng Diệu, Q.Hải Châu, Đà Nẵng', 'Kèm hóa đơn bán lẻ', 1125000.00, 'VNPAY', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 29 DAY), DATE_SUB(NOW(6), INTERVAL 27 DAY)),
('30000000-0000-0000-0000-000000000041', 'ORD26000041', '00000000-0000-0000-0000-000000000003', 'Nguyễn Thị Lan', '0987654321', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', NULL, 655000.00, 'COD', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 30 DAY), DATE_SUB(NOW(6), INTERVAL 28 DAY)),
('30000000-0000-0000-0000-000000000042', 'ORD26000042', NULL, 'Châu Bảo Trâm', '0961678916', '64 Đại lộ Bình Dương, TP.Thủ Dầu Một', 'Giao trước 16h', 910000.00, 'VNPAY', 'PAID', 'PROCESSING', FALSE, DATE_SUB(NOW(6), INTERVAL 32 DAY), DATE_SUB(NOW(6), INTERVAL 31 DAY)),
('30000000-0000-0000-0000-000000000043', 'ORD26000043', NULL, 'Lương Gia Huy', '0972789017', '310 Tô Hiến Thành, Q.10, TP.HCM', 'Đơn mua hộ đồng nghiệp', 700000.00, 'BANKING', 'PAID', 'SHIPPED', FALSE, DATE_SUB(NOW(6), INTERVAL 34 DAY), DATE_SUB(NOW(6), INTERVAL 33 DAY)),
('30000000-0000-0000-0000-000000000044', 'ORD26000044', NULL, 'Kiều Phương Anh', '0983890118', '128 Kim Mã, Q.Ba Đình, Hà Nội', 'Khách đặt nhầm màu son', 160000.00, 'COD', 'UNPAID', 'CANCELLED', FALSE, DATE_SUB(NOW(6), INTERVAL 36 DAY), DATE_SUB(NOW(6), INTERVAL 35 DAY)),
('30000000-0000-0000-0000-000000000045', 'ORD26000045', NULL, 'Tô Minh Quân', '0904901219', '55 Đường 3/2, Q.Ninh Kiều, Cần Thơ', NULL, 896000.00, 'COD', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 38 DAY), DATE_SUB(NOW(6), INTERVAL 36 DAY)),
('30000000-0000-0000-0000-000000000046', 'ORD26000046', '00000000-0000-0000-0000-000000000002', 'Nguyễn Thu Hà', '0912345678', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Gói riêng từng món', 1335000.00, 'VNPAY', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 40 DAY), DATE_SUB(NOW(6), INTERVAL 38 DAY)),
('30000000-0000-0000-0000-000000000047', 'ORD26000047', NULL, 'Lưu Thanh Hằng', '0915012320', '40 Quang Trung, Q.Hà Đông, Hà Nội', 'Giao buổi sáng', 1260000.00, 'COD', 'UNPAID', 'PENDING', FALSE, DATE_SUB(NOW(6), INTERVAL 42 DAY), DATE_SUB(NOW(6), INTERVAL 42 DAY)),
('30000000-0000-0000-0000-000000000048', 'ORD26000048', NULL, 'Quách Trọng Nghĩa', '0926123421', '82 Lê Văn Sỹ, Q.3, TP.HCM', NULL, 630000.00, 'BANKING', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 45 DAY), DATE_SUB(NOW(6), INTERVAL 43 DAY)),
('30000000-0000-0000-0000-000000000049', 'ORD26000049', NULL, 'Vương Thùy Dương', '0937234522', '11 Hùng Vương, TP.Thủ Dầu Một, Bình Dương', 'Đặt cho gia đình', 1890000.00, 'VNPAY', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 50 DAY), DATE_SUB(NOW(6), INTERVAL 48 DAY)),
('30000000-0000-0000-0000-000000000050', 'ORD26000050', '00000000-0000-0000-0000-000000000003', 'Nguyễn Thị Lan', '0987654321', '12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM', 'Đơn hàng tích điểm thành viên', 1318000.00, 'BANKING', 'PAID', 'DELIVERED', FALSE, DATE_SUB(NOW(6), INTERVAL 55 DAY), DATE_SUB(NOW(6), INTERVAL 53 DAY))
ON DUPLICATE KEY UPDATE `status` = VALUES(`status`), `total_amount` = VALUES(`total_amount`), `is_deleted` = VALUES(`is_deleted`);

-- -----------------------------------------------------------------------------
-- 6. Table: order_items
-- Surrogate Key (id): Chuẩn UUID 36 ký tự tiền tố 40000000-...
-- Foreign Key (order_id): Trỏ chính xác tới orders(id) chuẩn UUID 36 ký tự
-- Foreign Key (product_id): Trỏ chính xác tới products(id) chuẩn UUID 36 ký tự
-- -----------------------------------------------------------------------------
INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `product_name`, `product_code`, `unit_price`, `quantity`, `subtotal`) VALUES
('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 1, 450000.00),
('40000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 2, 400000.00),
('40000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000003', 'Son dưỡng môi DHC Lip Cream', 'SP26000003', 160000.00, 2, 320000.00),
('40000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 2, 1450000.00),
('40000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 1, 590000.00),
('40000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 1, 210000.00),
('40000000-0000-0000-0000-000000000007', '30000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 2, 320000.00),
('40000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 'SP26000008', 245000.00, 1, 245000.00),
('40000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000010', 'Tinh Chất Rau Má Skin1004 Madagascar Centella Ampoule 100ml', 'SP26000010', 335000.00, 2, 670000.00),
('40000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000009', 'Kem Chống Nắng Vichy Thoáng Nhẹ Không Bóng Dầu SPF 50 50ml', 'SP26000009', 348000.00, 1, 348000.00),
('40000000-0000-0000-0000-000000000011', '30000000-0000-0000-0000-000000000008', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 2, 900000.00),
('40000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000008', '20000000-0000-0000-0000-000000000003', 'Son dưỡng môi DHC Lip Cream', 'SP26000003', 160000.00, 1, 160000.00),
('40000000-0000-0000-0000-000000000013', '30000000-0000-0000-0000-000000000009', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 1, 725000.00),
('40000000-0000-0000-0000-000000000014', '30000000-0000-0000-0000-000000000009', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 1, 590000.00),
('40000000-0000-0000-0000-000000000015', '30000000-0000-0000-0000-000000000009', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 1, 210000.00),
('40000000-0000-0000-0000-000000000016', '30000000-0000-0000-0000-000000000010', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 3, 600000.00),
('40000000-0000-0000-0000-000000000017', '30000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 2, 1180000.00),
('40000000-0000-0000-0000-000000000018', '30000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 1, 160000.00),
('40000000-0000-0000-0000-000000000019', '30000000-0000-0000-0000-000000000012', '20000000-0000-0000-0000-000000000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 'SP26000008', 245000.00, 2, 490000.00),
('40000000-0000-0000-0000-000000000020', '30000000-0000-0000-0000-000000000013', '20000000-0000-0000-0000-000000000009', 'Kem Chống Nắng Vichy Thoáng Nhẹ Không Bóng Dầu SPF 50 50ml', 'SP26000009', 348000.00, 2, 696000.00),
('40000000-0000-0000-0000-000000000021', '30000000-0000-0000-0000-000000000013', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 2, 420000.00),
('40000000-0000-0000-0000-000000000022', '30000000-0000-0000-0000-000000000014', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 2, 1450000.00),
('40000000-0000-0000-0000-000000000023', '30000000-0000-0000-0000-000000000014', '20000000-0000-0000-0000-000000000010', 'Tinh Chất Rau Má Skin1004 Madagascar Centella Ampoule 100ml', 'SP26000010', 335000.00, 1, 335000.00),
('40000000-0000-0000-0000-000000000024', '30000000-0000-0000-0000-000000000014', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 1, 450000.00),
('40000000-0000-0000-0000-000000000025', '30000000-0000-0000-0000-000000000015', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 3, 480000.00),
('40000000-0000-0000-0000-000000000026', '30000000-0000-0000-0000-000000000015', '20000000-0000-0000-0000-000000000003', 'Son dưỡng môi DHC Lip Cream', 'SP26000003', 160000.00, 2, 320000.00),
('40000000-0000-0000-0000-000000000027', '30000000-0000-0000-0000-000000000016', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 1, 590000.00),
('40000000-0000-0000-0000-000000000028', '30000000-0000-0000-0000-000000000016', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 1, 200000.00),
('40000000-0000-0000-0000-000000000029', '30000000-0000-0000-0000-000000000016', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 1, 210000.00),
('40000000-0000-0000-0000-000000000030', '30000000-0000-0000-0000-000000000017', '20000000-0000-0000-0000-000000000010', 'Tinh Chất Rau Má Skin1004 Madagascar Centella Ampoule 100ml', 'SP26000010', 335000.00, 2, 670000.00),
('40000000-0000-0000-0000-000000000031', '30000000-0000-0000-0000-000000000018', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 1, 450000.00),
('40000000-0000-0000-0000-000000000032', '30000000-0000-0000-0000-000000000019', '20000000-0000-0000-0000-000000000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 'SP26000008', 245000.00, 1, 245000.00),
('40000000-0000-0000-0000-000000000033', '30000000-0000-0000-0000-000000000019', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 2, 320000.00),
('40000000-0000-0000-0000-000000000034', '30000000-0000-0000-0000-000000000019', '20000000-0000-0000-0000-000000000009', 'Kem Chống Nắng Vichy Thoáng Nhẹ Không Bóng Dầu SPF 50 50ml', 'SP26000009', 348000.00, 1, 348000.00),
('40000000-0000-0000-0000-000000000035', '30000000-0000-0000-0000-000000000020', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 1, 725000.00),
('40000000-0000-0000-0000-000000000036', '30000000-0000-0000-0000-000000000020', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 1, 450000.00),
('40000000-0000-0000-0000-000000000037', '30000000-0000-0000-0000-000000000020', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 1, 200000.00),
('40000000-0000-0000-0000-000000000038', '30000000-0000-0000-0000-000000000021', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 1, 590000.00),
('40000000-0000-0000-0000-000000000039', '30000000-0000-0000-0000-000000000021', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 1, 200000.00),
('40000000-0000-0000-0000-000000000040', '30000000-0000-0000-0000-000000000022', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 1, 725000.00),
('40000000-0000-0000-0000-000000000041', '30000000-0000-0000-0000-000000000022', '20000000-0000-0000-0000-000000000010', 'Tinh Chất Rau Má Skin1004 Madagascar Centella Ampoule 100ml', 'SP26000010', 335000.00, 1, 335000.00),
('40000000-0000-0000-0000-000000000042', '30000000-0000-0000-0000-000000000023', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 1, 450000.00),
('40000000-0000-0000-0000-000000000043', '30000000-0000-0000-0000-000000000023', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 2, 420000.00),
('40000000-0000-0000-0000-000000000044', '30000000-0000-0000-0000-000000000024', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 2, 320000.00),
('40000000-0000-0000-0000-000000000045', '30000000-0000-0000-0000-000000000024', '20000000-0000-0000-0000-000000000003', 'Son dưỡng môi DHC Lip Cream', 'SP26000003', 160000.00, 1, 160000.00),
('40000000-0000-0000-0000-000000000046', '30000000-0000-0000-0000-000000000025', '20000000-0000-0000-0000-000000000009', 'Kem Chống Nắng Vichy Thoáng Nhẹ Không Bóng Dầu SPF 50 50ml', 'SP26000009', 348000.00, 2, 696000.00),
('40000000-0000-0000-0000-000000000047', '30000000-0000-0000-0000-000000000026', '20000000-0000-0000-0000-000000000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 'SP26000008', 245000.00, 2, 490000.00),
('40000000-0000-0000-0000-000000000048', '30000000-0000-0000-0000-000000000026', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 1, 160000.00),
('40000000-0000-0000-0000-000000000049', '30000000-0000-0000-0000-000000000027', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 1, 590000.00),
('40000000-0000-0000-0000-000000000050', '30000000-0000-0000-0000-000000000027', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 1, 210000.00),
('40000000-0000-0000-0000-000000000051', '30000000-0000-0000-0000-000000000028', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 2, 1450000.00),
('40000000-0000-0000-0000-000000000052', '30000000-0000-0000-0000-000000000029', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 2, 400000.00),
('40000000-0000-0000-0000-000000000053', '30000000-0000-0000-0000-000000000030', '20000000-0000-0000-0000-000000000010', 'Tinh Chất Rau Má Skin1004 Madagascar Centella Ampoule 100ml', 'SP26000010', 335000.00, 2, 670000.00),
('40000000-0000-0000-0000-000000000054', '30000000-0000-0000-0000-000000000030', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 1, 450000.00),
('40000000-0000-0000-0000-000000000055', '30000000-0000-0000-0000-000000000031', '20000000-0000-0000-0000-000000000003', 'Son dưỡng môi DHC Lip Cream', 'SP26000003', 160000.00, 3, 480000.00),
('40000000-0000-0000-0000-000000000056', '30000000-0000-0000-0000-000000000031', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 1, 160000.00),
('40000000-0000-0000-0000-000000000057', '30000000-0000-0000-0000-000000000032', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 2, 1180000.00),
('40000000-0000-0000-0000-000000000058', '30000000-0000-0000-0000-000000000032', '20000000-0000-0000-0000-000000000009', 'Kem Chống Nắng Vichy Thoáng Nhẹ Không Bóng Dầu SPF 50 50ml', 'SP26000009', 348000.00, 1, 348000.00),
('40000000-0000-0000-0000-000000000059', '30000000-0000-0000-0000-000000000033', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 2, 420000.00),
('40000000-0000-0000-0000-000000000060', '30000000-0000-0000-0000-000000000033', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 1, 200000.00),
('40000000-0000-0000-0000-000000000061', '30000000-0000-0000-0000-000000000034', '20000000-0000-0000-0000-000000000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 'SP26000008', 245000.00, 1, 245000.00),
('40000000-0000-0000-0000-000000000062', '30000000-0000-0000-0000-000000000034', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 1, 450000.00),
('40000000-0000-0000-0000-000000000063', '30000000-0000-0000-0000-000000000035', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 1, 725000.00),
('40000000-0000-0000-0000-000000000064', '30000000-0000-0000-0000-000000000035', '20000000-0000-0000-0000-000000000003', 'Son dưỡng môi DHC Lip Cream', 'SP26000003', 160000.00, 2, 320000.00),
('40000000-0000-0000-0000-000000000065', '30000000-0000-0000-0000-000000000036', '20000000-0000-0000-0000-000000000010', 'Tinh Chất Rau Má Skin1004 Madagascar Centella Ampoule 100ml', 'SP26000010', 335000.00, 1, 335000.00),
('40000000-0000-0000-0000-000000000066', '30000000-0000-0000-0000-000000000037', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 2, 320000.00),
('40000000-0000-0000-0000-000000000067', '30000000-0000-0000-0000-000000000037', '20000000-0000-0000-0000-000000000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 'SP26000008', 245000.00, 1, 245000.00),
('40000000-0000-0000-0000-000000000068', '30000000-0000-0000-0000-000000000037', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 1, 590000.00),
('40000000-0000-0000-0000-000000000069', '30000000-0000-0000-0000-000000000038', '20000000-0000-0000-0000-000000000009', 'Kem Chống Nắng Vichy Thoáng Nhẹ Không Bóng Dầu SPF 50 50ml', 'SP26000009', 348000.00, 1, 348000.00),
('40000000-0000-0000-0000-000000000070', '30000000-0000-0000-0000-000000000038', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 1, 210000.00),
('40000000-0000-0000-0000-000000000071', '30000000-0000-0000-0000-000000000039', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 2, 900000.00),
('40000000-0000-0000-0000-000000000072', '30000000-0000-0000-0000-000000000040', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 1, 725000.00),
('40000000-0000-0000-0000-000000000073', '30000000-0000-0000-0000-000000000040', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 2, 400000.00),
('40000000-0000-0000-0000-000000000074', '30000000-0000-0000-0000-000000000041', '20000000-0000-0000-0000-000000000003', 'Son dưỡng môi DHC Lip Cream', 'SP26000003', 160000.00, 2, 320000.00),
('40000000-0000-0000-0000-000000000075', '30000000-0000-0000-0000-000000000041', '20000000-0000-0000-0000-000000000010', 'Tinh Chất Rau Má Skin1004 Madagascar Centella Ampoule 100ml', 'SP26000010', 335000.00, 1, 335000.00),
('40000000-0000-0000-0000-000000000076', '30000000-0000-0000-0000-000000000042', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 1, 590000.00),
('40000000-0000-0000-0000-000000000077', '30000000-0000-0000-0000-000000000042', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 2, 320000.00),
('40000000-0000-0000-0000-000000000078', '30000000-0000-0000-0000-000000000043', '20000000-0000-0000-0000-000000000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 'SP26000008', 245000.00, 2, 490000.00),
('40000000-0000-0000-0000-000000000079', '30000000-0000-0000-0000-000000000043', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 1, 210000.00),
('40000000-0000-0000-0000-000000000080', '30000000-0000-0000-0000-000000000044', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 'SP26000007', 160000.00, 1, 160000.00),
('40000000-0000-0000-0000-000000000081', '30000000-0000-0000-0000-000000000045', '20000000-0000-0000-0000-000000000009', 'Kem Chống Nắng Vichy Thoáng Nhẹ Không Bóng Dầu SPF 50 50ml', 'SP26000009', 348000.00, 2, 696000.00),
('40000000-0000-0000-0000-000000000082', '30000000-0000-0000-0000-000000000045', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 1, 200000.00),
('40000000-0000-0000-0000-000000000083', '30000000-0000-0000-0000-000000000046', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 1, 725000.00),
('40000000-0000-0000-0000-000000000084', '30000000-0000-0000-0000-000000000046', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 1, 450000.00),
('40000000-0000-0000-0000-000000000085', '30000000-0000-0000-0000-000000000046', '20000000-0000-0000-0000-000000000003', 'Son dưỡng môi DHC Lip Cream', 'SP26000003', 160000.00, 1, 160000.00),
('40000000-0000-0000-0000-000000000086', '30000000-0000-0000-0000-000000000047', '20000000-0000-0000-0000-000000000010', 'Tinh Chất Rau Má Skin1004 Madagascar Centella Ampoule 100ml', 'SP26000010', 335000.00, 2, 670000.00),
('40000000-0000-0000-0000-000000000087', '30000000-0000-0000-0000-000000000047', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 1, 590000.00),
('40000000-0000-0000-0000-000000000088', '30000000-0000-0000-0000-000000000048', '20000000-0000-0000-0000-000000000006', 'Nước tẩy trang Bioderma Sensibio H2O 250ml', 'SP26000006', 210000.00, 3, 630000.00),
('40000000-0000-0000-0000-000000000089', '30000000-0000-0000-0000-000000000049', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 'SP26000001', 450000.00, 2, 900000.00),
('40000000-0000-0000-0000-000000000090', '30000000-0000-0000-0000-000000000049', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 'SP26000002', 200000.00, 2, 400000.00),
('40000000-0000-0000-0000-000000000091', '30000000-0000-0000-0000-000000000049', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 'SP26000005', 590000.00, 1, 590000.00),
('40000000-0000-0000-0000-000000000092', '30000000-0000-0000-0000-000000000050', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 'SP26000004', 725000.00, 1, 725000.00),
('40000000-0000-0000-0000-000000000093', '30000000-0000-0000-0000-000000000050', '20000000-0000-0000-0000-000000000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 'SP26000008', 245000.00, 1, 245000.00),
('40000000-0000-0000-0000-000000000094', '30000000-0000-0000-0000-000000000050', '20000000-0000-0000-0000-000000000009', 'Kem Chống Nắng Vichy Thoáng Nhẹ Không Bóng Dầu SPF 50 50ml', 'SP26000009', 348000.00, 1, 348000.00)
ON DUPLICATE KEY UPDATE `quantity` = VALUES(`quantity`), `subtotal` = VALUES(`subtotal`), `unit_price` = VALUES(`unit_price`), `product_id` = VALUES(`product_id`), `product_name` = VALUES(`product_name`), `product_code` = VALUES(`product_code`);

-- -----------------------------------------------------------------------------
-- 7. Table: user_activity_logs (Bản ghi mẫu theo dõi phiên & phân tích gợi ý AI)
-- Surrogate Key (id): Chuẩn UUID 36 ký tự tiền tố 50000000-...
-- Foreign Key (user_id): Trỏ chính xác tới users(id) chuẩn UUID 36 ký tự (hoặc NULL)
-- Foreign Key (product_id): Trỏ chính xác tới products(id) chuẩn UUID 36 ký tự
-- -----------------------------------------------------------------------------
INSERT INTO `user_activity_logs` (`id`, `session_id`, `user_id`, `event_type`, `action`, `product_id`, `product_name`, `click_count`, `dwell_seconds`, `add_to_cart_count`, `page_url`, `occurred_at`) VALUES
('50000000-0000-0000-0000-000000000001', 'demo-session-pinky-001', NULL, 'PAGE_VIEW', 'PAGE_VIEW', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 1, 45, 0, '/san-pham/20000000-0000-0000-0000-000000000005', DATE_SUB(NOW(6), INTERVAL 30 MINUTE)),
('50000000-0000-0000-0000-000000000002', 'demo-session-pinky-001', NULL, 'PRODUCT_CLICK', 'CLICK_PRODUCT', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 2, 60, 0, '/san-pham/20000000-0000-0000-0000-000000000005', DATE_SUB(NOW(6), INTERVAL 28 MINUTE)),
('50000000-0000-0000-0000-000000000003', 'demo-session-pinky-001', NULL, 'ADD_TO_CART', 'ADD_TO_CART', '20000000-0000-0000-0000-000000000005', 'Kem chống nắng Anessa Perfect UV Sunscreen Skincare Milk SPF50+ PA++++', 1, 0, 1, '/cart', DATE_SUB(NOW(6), INTERVAL 25 MINUTE)),
('50000000-0000-0000-0000-000000000004', 'demo-session-pinky-001', NULL, 'PAGE_VIEW', 'PAGE_VIEW', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 3, 90, 0, '/san-pham/20000000-0000-0000-0000-000000000004', DATE_SUB(NOW(6), INTERVAL 20 MINUTE)),
('50000000-0000-0000-0000-000000000005', 'demo-session-pinky-001', NULL, 'ADD_TO_CART', 'BUY_NOW', '20000000-0000-0000-0000-000000000004', 'Serum Phục Hồi Tái Tạo Da La Roche-Posay Hyalu B5', 1, 0, 1, '/checkout', DATE_SUB(NOW(6), INTERVAL 18 MINUTE)),

('50000000-0000-0000-0000-000000000006', 'demo-session-pinky-002', NULL, 'PAGE_VIEW', 'PAGE_VIEW', '20000000-0000-0000-0000-000000000008', 'Kem Nền Maybelline Fit Me Matte + Poreless', 2, 55, 0, '/san-pham/20000000-0000-0000-0000-000000000008', DATE_SUB(NOW(6), INTERVAL 15 MINUTE)),
('50000000-0000-0000-0000-000000000007', 'demo-session-pinky-002', NULL, 'PRODUCT_CLICK', 'CLICK_PRODUCT', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 3, 80, 0, '/san-pham/20000000-0000-0000-0000-000000000007', DATE_SUB(NOW(6), INTERVAL 10 MINUTE)),
('50000000-0000-0000-0000-000000000008', 'demo-session-pinky-002', NULL, 'ADD_TO_CART', 'ADD_TO_CART', '20000000-0000-0000-0000-000000000007', 'Son Kem Black Rouge Air Fit Velvet Tint A12 Dashed Brown', 1, 0, 2, '/cart', DATE_SUB(NOW(6), INTERVAL 8 MINUTE)),

('50000000-0000-0000-0000-000000000009', 'demo-session-customer-003', '00000000-0000-0000-0000-000000000002', 'PAGE_VIEW', 'PAGE_VIEW', '20000000-0000-0000-0000-000000000001', 'Kem dưỡng ẩm Laneige Water Bank Blue Hyaluronic Cream', 4, 110, 0, '/san-pham/20000000-0000-0000-0000-000000000001', DATE_SUB(NOW(6), INTERVAL 5 MINUTE)),
('50000000-0000-0000-0000-000000000010', 'demo-session-customer-003', '00000000-0000-0000-0000-000000000002', 'ADD_TO_CART', 'ADD_TO_CART', '20000000-0000-0000-0000-000000000002', 'Sữa rửa mặt Cetaphil Gentle Skin Cleanser Dịu Lành', 1, 35, 1, '/cart', DATE_SUB(NOW(6), INTERVAL 2 MINUTE))
ON DUPLICATE KEY UPDATE `click_count` = VALUES(`click_count`), `dwell_seconds` = VALUES(`dwell_seconds`);

-- -----------------------------------------------------------------------------
-- 8. Table: news_articles
-- -----------------------------------------------------------------------------
INSERT INTO `news_articles` (`slug`, `title`, `excerpt`, `content`, `published_date`, `category`, `author`, `author_role`, `read_time`, `image_url`, `linked_product_ids`, `tags`, `views_count`, `is_featured`, `active`, `is_deleted`, `created_at`, `updated_at`) VALUES
('top-kem-chong-nang-cho-da-dau-mun-2026', 
'Top 5 Kem Chống Nắng Kiềm Dầu Chống Nước Tốt Nhất 2026', 
'Khám phá danh sách các dòng kem chống nắng quang phổ rộng đỉnh cao dành riêng cho làn da dầu mụn và nhạy cảm.',
'Chống nắng là bước không thể thiếu trong chu trình dưỡng da hằng ngày. Đối với làn da dầu mụn, việc chọn sản phẩm mỏng nhẹ, không gây bít tắc lỗ chân lông là tiêu chí hàng đầu. Điển hình như dòng Anessa Skincare Milk hay Vichy Dry Touch...',
'15/01/2026', 'Cẩm nang làm đẹp', 'Pinky Editor', 'Chuyên gia Da liễu', '5 phút đọc', '/IMG/news01.png', '20000000-0000-0000-0000-000000000005,20000000-0000-0000-0000-000000000009', 'kem chống nắng, kiềm dầu, da nhạy cảm', 1520, TRUE, TRUE, FALSE, NOW(6), NOW(6)),

('bi-quyet-phuc-hoi-da-treatment-b5', 
'Bí Quyết Phục Hồi Làn Da Đang Treatment Cùng Serum Vitamin B5', 
'Cách kết hợp Serum Hyalu B5 và kem dưỡng ẩm đa tầng giúp làn da căng bóng, khỏe mạnh và giảm ửng đỏ tức thì.',
'Làn da treatment với BHA, Retinol rất cần dưỡng chất phục hồi sâu. Bộ đôi Vitamin B5 và Hyaluronic Acid là cứu cánh hoàn hảo giúp tái tạo biểu bì mô da và duy trì độ đàn hồi.',
'20/02/2026', 'Chăm sóc da', 'Bác sĩ Thanh Thảo', 'Cố vấn chuyên môn', '7 phút đọc', '/IMG/news02.png', '20000000-0000-0000-0000-000000000004,20000000-0000-0000-0000-000000000001', 'phục hồi da, treatment, vitamin b5', 2340, TRUE, TRUE, FALSE, NOW(6), NOW(6)),

('huong-dan-danh-kem-nen-khong-bi-moc-cakey', 
'Hướng Dẫn Đánh Kem Nền Mịn Lì Suốt 16 Giờ Không Mốc Da', 
'Mẹo trang điểm chuyên nghiệp từ makeup artist giúp lớp nền Maybelline Fit Me bám chặt và đều màu tự nhiên.',
'Để có lớp nền tiệp vào da không tì vết, bước chuẩn bị dưỡng ẩm kỹ lưỡng trước khi makeup là yếu tố quyết định. Hãy dùng mút ẩm dặm từng lớp mỏng thay vì thoa miết trên da.',
'10/03/2026', 'Xu hướng trang điểm', 'Mai Anh Makeup', 'Makeup Artist', '4 phút đọc', '/IMG/news03.png', '20000000-0000-0000-0000-000000000008,20000000-0000-0000-0000-000000000007', 'trang điểm, kem nền, son lì', 3120, FALSE, TRUE, FALSE, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`), `views_count` = VALUES(`views_count`), `is_deleted` = VALUES(`is_deleted`);

-- -----------------------------------------------------------------------------
-- 9. Table: offices & office_perks
-- Surrogate Key (id): Chuẩn UUID 36 ký tự tiền tố 60000000-...
-- Foreign Key (office_id): Trỏ chính xác tới offices(id) chuẩn UUID 36 ký tự
-- -----------------------------------------------------------------------------
INSERT INTO `offices` (`id`, `title`, `address`, `description`, `city`, `phone`, `email`, `working_hours`, `support_type`, `coupon`, `image`, `active`, `is_deleted`) VALUES
('60000000-0000-0000-0000-000000000001', 'Showroom PinkyCloud Gò Vấp - TP.HCM', '12 Nguyễn Văn Bảo, Phường 4, Quận Gò Vấp, TP. Hồ Chí Minh', 'Showroom flagship trải nghiệm mỹ phẩm cao cấp với công nghệ soi da AI độc quyền.', 'Hồ Chí Minh', '028 3894 0390', 'govap@pinkycloud.com', '08:30 - 21:30', 'Tư vấn trực tiếp & Đổi trả', 'PINKYHCM10', '/IMG/news04.png', TRUE, FALSE),
('60000000-0000-0000-0000-000000000002', 'Showroom PinkyCloud Hoàn Kiếm - Hà Nội', '45 Tràng Tiền, Quận Hoàn Kiếm, Hà Nội', 'Không gian mua sắm mỹ phẩm chính hãng tại trung tâm thủ đô.', 'Hà Nội', '024 3936 8888', 'hanoi@pinkycloud.com', '09:00 - 22:00', 'Tư vấn trực tiếp & Giao hỏa tốc', 'PINKYHN10', '/IMG/news05.png', TRUE, FALSE),
('60000000-0000-0000-0000-000000000003', 'Showroom PinkyCloud Hải Châu - Đà Nẵng', '102 Bạch Đằng, Quận Hải Châu, Đà Nẵng', 'Chi nhánh phục vụ khách hàng khu vực miền Trung với đầy đủ các thương hiệu hàng đầu.', 'Đà Nẵng', '0236 388 9999', 'danang@pinkycloud.com', '08:30 - 21:30', 'Tư vấn trực tiếp', 'PINKYDN10', '/IMG/news06.png', TRUE, FALSE)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`), `address` = VALUES(`address`), `is_deleted` = VALUES(`is_deleted`);

INSERT INTO `office_perks` (`office_id`, `perk`) VALUES
('60000000-0000-0000-0000-000000000001', 'Soi da AI và tư vấn phác đồ miễn phí'),
('60000000-0000-0000-0000-000000000001', 'Khu vực thử nghiệm test mỹ phẩm chuyên sâu'),
('60000000-0000-0000-0000-000000000001', 'Bãi đỗ xe ô tô và xe máy rộng rãi'),
('60000000-0000-0000-0000-000000000002', 'Soi da AI và tư vấn phác đồ miễn phí'),
('60000000-0000-0000-0000-000000000002', 'Dịch vụ trang điểm nhanh trước sự kiện'),
('60000000-0000-0000-0000-000000000002', 'Giao hàng hỏa tốc nội thành trong 2 giờ'),
('60000000-0000-0000-0000-000000000003', 'Tư vấn viên da liễu túc trực hỗ trợ'),
('60000000-0000-0000-0000-000000000003', 'Không gian trải nghiệm sang trọng và thân thiện');

-- -----------------------------------------------------------------------------
-- 10. Table: vouchers
-- -----------------------------------------------------------------------------
INSERT INTO `vouchers` (`code`, `title`, `detail`, `status`, `accent`, `discount_percent`, `discount_amount`, `min_order_amount`, `usage_limit`, `used_count`, `target_audience`, `badge_text`, `priority`, `active`, `is_deleted`, `created_at`, `updated_at`) VALUES
('WELCOME10', 'Giảm 10% Cho Đơn Hàng Đầu Tiên', 'Áp dụng cho mọi khách hàng mới đăng ký tài khoản tại PinkyCloud', 'active', 'linear-gradient(135deg, #fff1f5 0%, #ffd6e3 100%)', 10, NULL, 100000.00, 1000, 15, 'NEW_CUSTOMER', 'KHÁCH MỚI', 90, TRUE, FALSE, NOW(6), NOW(6)),
('PINKY50K', 'Giảm Trực Tiếp 50.000đ', 'Giảm ngay 50.000đ cho đơn hàng mỹ phẩm từ 300.000đ trở lên', 'active', 'linear-gradient(135deg, #f0f7ff 0%, #d4e8ff 100%)', NULL, 50000.00, 300000.00, 500, 42, 'ALL', 'GIẢM TIỀN MẶT', 80, TRUE, FALSE, NOW(6), NOW(6)),
('FREESHIP', 'Miễn Phí Vận Chuyển Toàn Quốc', 'Hỗ trợ tối đa 30.000đ phí giao hàng toàn quốc cho đơn từ 250.000đ', 'active', 'linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%)', NULL, 30000.00, 250000.00, 2000, 180, 'ALL', 'FREESHIP', 70, TRUE, FALSE, NOW(6), NOW(6)),
('BEAUTYVIP', 'Đặc Quyền Hội Viên VIP Giảm 15%', 'Ưu đãi dành riêng cho thành viên VIP của PinkyCloud', 'active', 'linear-gradient(135deg, #fff7e6 0%, #ffe7ba 100%)', 15, NULL, 500000.00, 200, 28, 'VIP_ONLY', 'VIP MEMBER', 100, TRUE, FALSE, NOW(6), NOW(6))
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`), `status` = VALUES(`status`), `active` = VALUES(`active`), `is_deleted` = VALUES(`is_deleted`);

SET FOREIGN_KEY_CHECKS = 1;
