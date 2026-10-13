-- 创建数据库
CREATE DATABASE IF NOT EXISTS tourism_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE tourism_management;

-- 创建用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建景点表
CREATE TABLE IF NOT EXISTS t_attraction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    location VARCHAR(200),
    city VARCHAR(50),
    province VARCHAR(50),
    ticket_price DECIMAL(10,2),
    opening_hours VARCHAR(200),
    images TEXT,
    rating INT,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建旅游线路表
CREATE TABLE IF NOT EXISTS t_tour_route (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    days INT NOT NULL,
    destinations TEXT,
    price DECIMAL(10,2) NOT NULL,
    transportation VARCHAR(100),
    accommodation VARCHAR(100),
    itinerary TEXT,
    images TEXT,
    max_participants INT,
    status VARCHAR(20) NOT NULL,
    departure_time DATETIME,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建订单表
CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(32) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    tour_route_id BIGINT NOT NULL,
    number_of_people INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    special_requirements TEXT,
    status VARCHAR(20) NOT NULL,
    order_time DATETIME NOT NULL,
    pay_time DATETIME,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES t_user(id),
    FOREIGN KEY (tour_route_id) REFERENCES t_tour_route(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入管理员用户
INSERT INTO t_user (username, password, role, create_time, update_time)
VALUES ('admin', '$2a$10$X/hX4qvWzxwRbHpWxS5kEOCaUmj0mKvPCxGw4.W6pH0DWbKxELjFi', 'ROLE_ADMIN', NOW(), NOW()); 