-- Xóa các bảng không cần thiết
DROP TABLE IF EXISTS import_details;
DROP TABLE IF EXISTS import_histories;
DROP TABLE IF EXISTS supplier_categories;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS settings;

-- Tạo bảng carts
CREATE TABLE carts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tạo bảng cart_details
CREATE TABLE cart_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (cart_id) REFERENCES carts(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Sửa đổi bảng products
ALTER TABLE products DROP FOREIGN KEY IF EXISTS products_ibfk_1;
ALTER TABLE products DROP COLUMN IF EXISTS supplier_id;
ALTER TABLE products DROP COLUMN IF EXISTS buy_price;

-- Sửa đổi bảng orders
ALTER TABLE orders DROP FOREIGN KEY IF EXISTS orders_ibfk_2;
ALTER TABLE orders DROP COLUMN IF EXISTS customer_id;
ALTER TABLE orders ADD COLUMN phone VARCHAR(20);
ALTER TABLE orders ADD COLUMN address VARCHAR(255);