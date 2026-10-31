-- =============================================================================
-- Database Schema for MariaDB (Single Source of Truth - SSOT)
-- Project: PinkyCloud Cosmetic Shop
-- Architectural Rule: Dual-Key Strategy (Surrogate Key UUID 36 chars + Business Key)
-- Character Set: utf8mb4 / utf8mb4_unicode_ci
-- =============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables in reverse order of dependency
DROP TABLE IF EXISTS `office_perks`;
DROP TABLE IF EXISTS `offices`;
DROP TABLE IF EXISTS `user_activity_logs`;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `vouchers`;
DROP TABLE IF EXISTS `news_articles`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `roles`;

-- -----------------------------------------------------------------------------
-- 1. Table: roles (Phân quyền người dùng hệ thống)
-- -----------------------------------------------------------------------------
CREATE TABLE `roles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `description` VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. Table: users (Tài khoản người dùng: Quản trị viên & Khách hàng)
-- Surrogate Key: id VARCHAR(36) - UUID chuẩn
-- Business Key:  user_code VARCHAR(20) - Mã hiển thị duy nhất (AD26..., KH26...)
-- -----------------------------------------------------------------------------
CREATE TABLE `users` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `user_code` VARCHAR(20) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NULL,
    `address` VARCHAR(255) NULL,
    `role` VARCHAR(30) NOT NULL DEFAULT 'ROLE_CUSTOMER',
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX `idx_users_code` (`user_code`),
    INDEX `idx_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. Table: categories (Danh mục sản phẩm mỹ phẩm)
-- Surrogate Key: id VARCHAR(36) - UUID chuẩn
-- Business Key:  category_code VARCHAR(20) - Mã hiển thị duy nhất (CAT001, CAT002...)
-- -----------------------------------------------------------------------------
CREATE TABLE `categories` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `category_code` VARCHAR(20) NOT NULL UNIQUE,
    `name` VARCHAR(150) NOT NULL,
    `description` VARCHAR(500) NULL,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX `idx_categories_code` (`category_code`),
    INDEX `idx_categories_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. Table: products (Sản phẩm mỹ phẩm)
-- Surrogate Key: id VARCHAR(36) - UUID chuẩn
-- Business Key:  product_code VARCHAR(30) - Mã hiển thị duy nhất (SP26000001...)
-- Foreign Key:   category_id VARCHAR(36) - Trỏ tới categories(id) chuẩn UUID
-- -----------------------------------------------------------------------------
CREATE TABLE `products` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `product_code` VARCHAR(30) NOT NULL UNIQUE,
    `name` VARCHAR(255) NOT NULL,
    `brand` VARCHAR(100) NOT NULL DEFAULT 'PinkyCloud',
    `price` DECIMAL(18, 2) NOT NULL,
    `discount` INT NOT NULL DEFAULT 0,
    `image_url` VARCHAR(255) NULL,
    `stock_quantity` INT NOT NULL DEFAULT 0,
    `currency` VARCHAR(10) NULL DEFAULT 'VND',
    `origin` VARCHAR(100) NULL,
    `description` LONGTEXT NULL,
    `ingredients` LONGTEXT NULL,
    `usage_instructions` LONGTEXT NULL,
    `barcode` VARCHAR(50) NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `is_hot` BOOLEAN NOT NULL DEFAULT FALSE,
    `is_new` BOOLEAN NOT NULL DEFAULT FALSE,
    `rating` DOUBLE NOT NULL DEFAULT 5.0,
    `review_count` INT NOT NULL DEFAULT 0,
    `sold_count` INT NOT NULL DEFAULT 0,
    `category_id` VARCHAR(36) NOT NULL,
    `created_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX `idx_products_code` (`product_code`),
    INDEX `idx_products_name` (`name`),
    INDEX `idx_products_brand` (`brand`),
    INDEX `idx_products_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. Table: orders (Đơn đặt hàng)
-- Surrogate Key: id VARCHAR(36) - UUID chuẩn
-- Business Key:  order_code VARCHAR(20) - Mã hiển thị duy nhất (ORD26000001...)
-- Foreign Key:   user_id VARCHAR(36) NULL - Trỏ tới users(id) chuẩn UUID (hỗ trợ khách vãng lai)
-- -----------------------------------------------------------------------------
CREATE TABLE `orders` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `order_code` VARCHAR(20) NOT NULL UNIQUE,
    `user_id` VARCHAR(36) NULL,
    `customer_name` VARCHAR(150) NOT NULL,
    `customer_phone` VARCHAR(20) NOT NULL,
    `shipping_address` VARCHAR(255) NOT NULL,
    `note` VARCHAR(500) NULL,
    `total_amount` DECIMAL(12, 2) NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL DEFAULT 'COD',
    `payment_status` VARCHAR(30) NOT NULL DEFAULT 'UNPAID',
    `status` VARCHAR(30) NOT NULL,
    `created_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX `idx_orders_code` (`order_code`),
    INDEX `idx_orders_status` (`status`),
    INDEX `idx_orders_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 6. Table: order_items (Chi tiết từng mục trong đơn hàng)
-- Surrogate Key: id VARCHAR(36) - UUID chuẩn
-- Foreign Key:   order_id VARCHAR(36) - Trỏ tới orders(id) chuẩn UUID
--                product_id VARCHAR(36) NULL - Trỏ tới products(id) chuẩn UUID
-- -----------------------------------------------------------------------------
CREATE TABLE `order_items` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `order_id` VARCHAR(36) NOT NULL,
    `product_id` VARCHAR(36) NULL,
    `product_name` VARCHAR(150) NOT NULL,
    `product_code` VARCHAR(20) NULL,
    `unit_price` DECIMAL(12, 2) NOT NULL,
    `quantity` INT NOT NULL,
    `subtotal` DECIMAL(12, 2) NOT NULL,
    INDEX `idx_order_items_order_id` (`order_id`),
    INDEX `idx_order_items_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 7. Table: user_activity_logs (Nhật ký hành vi người dùng & phân tích AI)
-- Surrogate Key: id VARCHAR(36) - UUID chuẩn
-- Foreign Key:   user_id VARCHAR(36) NULL - Trỏ tới users(id) chuẩn UUID
--                product_id VARCHAR(36) NULL - Trỏ tới products(id) chuẩn UUID
-- -----------------------------------------------------------------------------
CREATE TABLE `user_activity_logs` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `session_id` VARCHAR(128) NOT NULL,
    `user_id` VARCHAR(36) NULL,
    `event_type` VARCHAR(50) NOT NULL,
    `action` VARCHAR(50) NULL,
    `product_id` VARCHAR(36) NULL,
    `product_name` VARCHAR(255) NULL,
    `click_count` INT NOT NULL DEFAULT 0,
    `dwell_seconds` INT NOT NULL DEFAULT 0,
    `add_to_cart_count` INT NOT NULL DEFAULT 0,
    `page_url` VARCHAR(255) NULL,
    `occurred_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX `idx_activity_session` (`session_id`),
    INDEX `idx_activity_product` (`product_id`),
    INDEX `idx_activity_occurred_at` (`occurred_at`),
    INDEX `idx_activity_event_type` (`event_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 8. Table: news_articles (Bài viết tin tức, cẩm nang làm đẹp)
-- -----------------------------------------------------------------------------
CREATE TABLE `news_articles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `slug` VARCHAR(150) NOT NULL UNIQUE,
    `title` VARCHAR(255) NOT NULL,
    `excerpt` VARCHAR(1000) NULL,
    `content` LONGTEXT NULL,
    `published_date` VARCHAR(30) NULL,
    `category` VARCHAR(100) NULL,
    `author` VARCHAR(100) NULL,
    `author_role` VARCHAR(100) NULL,
    `read_time` VARCHAR(30) NULL,
    `image_url` VARCHAR(255) NULL,
    `linked_product_ids` VARCHAR(255) NULL,
    `tags` VARCHAR(255) NULL,
    `views_count` BIGINT NOT NULL DEFAULT 0,
    `is_featured` BOOLEAN NOT NULL DEFAULT FALSE,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX `idx_articles_slug` (`slug`),
    INDEX `idx_articles_category` (`category`),
    INDEX `idx_articles_active` (`active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 9. Table: offices (Hệ thống chi nhánh / showroom)
-- Surrogate Key: id VARCHAR(36) - UUID chuẩn
-- -----------------------------------------------------------------------------
CREATE TABLE `offices` (
    `id` VARCHAR(36) NOT NULL PRIMARY KEY,
    `title` VARCHAR(255) NOT NULL,
    `address` VARCHAR(500) NULL,
    `description` LONGTEXT NULL,
    `city` VARCHAR(100) NULL,
    `phone` VARCHAR(50) NULL,
    `email` VARCHAR(100) NULL,
    `working_hours` VARCHAR(100) NULL,
    `support_type` VARCHAR(100) NULL,
    `coupon` VARCHAR(50) NULL,
    `image` VARCHAR(500) NULL,
    `active` BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 10. Table: office_perks (Đặc quyền / tiện ích tại chi nhánh)
-- Foreign Key: office_id VARCHAR(36) - Trỏ tới offices(id) chuẩn UUID
-- -----------------------------------------------------------------------------
CREATE TABLE `office_perks` (
    `office_id` VARCHAR(36) NOT NULL,
    `perk` VARCHAR(255) NOT NULL,
    INDEX `idx_office_perks_office_id` (`office_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 11. Table: vouchers (Mã ưu đãi / khuyến mãi)
-- Business Key: code VARCHAR(50) - Mã hiển thị duy nhất (WELCOME10, PINKY50K...)
-- -----------------------------------------------------------------------------
CREATE TABLE `vouchers` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(50) NOT NULL UNIQUE,
    `title` VARCHAR(255) NOT NULL,
    `detail` VARCHAR(255) NULL,
    `status` VARCHAR(30) NULL DEFAULT 'active',
    `accent` VARCHAR(255) NULL,
    `discount_percent` INT NULL,
    `discount_amount` DECIMAL(18, 2) NULL,
    `min_order_amount` DECIMAL(18, 2) NULL DEFAULT 0.00,
    `usage_limit` INT NULL DEFAULT 1000,
    `used_count` INT NOT NULL DEFAULT 0,
    `target_audience` VARCHAR(50) NULL DEFAULT 'ALL',
    `badge_text` VARCHAR(100) NULL,
    `priority` INT NOT NULL DEFAULT 50,
    `active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX `idx_vouchers_code` (`code`),
    INDEX `idx_vouchers_status` (`status`),
    INDEX `idx_vouchers_active` (`active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
