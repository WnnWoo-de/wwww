CREATE DATABASE IF NOT EXISTS `volunteer_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `volunteer_db`;

-- 用户表
CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(100) UNIQUE,
  `phone` VARCHAR(20) UNIQUE,
  `role` ENUM('user', 'admin') NOT NULL DEFAULT 'user' COMMENT '用户角色',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `points` INT DEFAULT 0 COMMENT '积分',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_email` (`email`),
  INDEX `idx_phone` (`phone`)
) COMMENT='用户表';

-- 活动表
CREATE TABLE `activities` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL,
  `description` TEXT NOT NULL,
  `location` VARCHAR(255),
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `required_volunteers` INT NOT NULL,
  `organizer_id` BIGINT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` INT DEFAULT 0,
  FOREIGN KEY (`organizer_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
) COMMENT='活动表';

-- 报名表
CREATE TABLE `registrations` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `activity_id` BIGINT NOT NULL,
  `status` ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'COMPLETED') DEFAULT 'PENDING',
  `registration_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`activity_id`) REFERENCES `activities`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_activity_id` (`activity_id`)
) COMMENT='报名表';

-- 积分商城物品表
CREATE TABLE `store_items` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `cost` INT NOT NULL,
  `stock` INT NOT NULL,
  `image_url` VARCHAR(255),
  `version` INT DEFAULT 0
) COMMENT='积分商城物品表';

-- 积分兑换记录表
CREATE TABLE `exchange_logs` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `item_id` BIGINT NOT NULL,
  `points_spent` INT NOT NULL,
  `exchange_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`item_id`) REFERENCES `store_items`(`id`)
) COMMENT='积分兑换记录表';

-- 插入用户数据
INSERT INTO `users` (`username`, `password`, `email`, `phone`, `role`, `avatar`, `points`) VALUES
                                                                                               ('user1', 'password1', 'user1@example.com', '12345678901', 'user', 'avatar1.jpg', 100),
                                                                                               ('user2', 'password2', 'user2@example.com', '12345678902', 'user', 'avatar2.jpg', 200),
                                                                                               ('user3', 'password3', 'user3@example.com', '12345678903', 'user', 'avatar3.jpg', 150),
                                                                                               ('admin1', 'adminpass1', 'admin1@example.com', '12345678904', 'admin', 'admin1.jpg', 500),
                                                                                               ('user4', 'password4', 'user4@example.com', '12345678905', 'user', 'avatar4.jpg', 300);

-- 插入活动数据
INSERT INTO `activities` (`title`, `description`, `location`, `start_time`, `end_time`, `required_volunteers`, `organizer_id`) VALUES
                                                                                                                                   ('社区清洁活动', '清理社区垃圾，美化环境', '社区公园', '2025-07-15 09:00:00', '2025-07-15 12:00:00', 10, 4),
                                                                                                                                   ('敬老院慰问', '为老人送去温暖和关怀', '阳光敬老院', '2025-07-20 14:00:00', '2025-07-20 17:00:00', 5, 4),
                                                                                                                                   ('环保宣传', '向市民宣传环保知识', '市中心广场', '2025-07-25 10:00:00', '2025-07-25 16:00:00', 8, 4),
                                                                                                                                   ('图书整理', '整理图书馆书籍', '市图书馆', '2025-08-01 09:30:00', '2025-08-01 15:30:00', 6, 4),
                                                                                                                                   ('植树活动', '为城市增添绿色', '郊区林场', '2025-08-10 08:00:00', '2025-08-10 12:00:00', 15, 4);

-- 插入报名数据
INSERT INTO `registrations` (`user_id`, `activity_id`, `status`) VALUES
                                                                     (1, 1, 'APPROVED'),
                                                                     (2, 1, 'PENDING'),
                                                                     (3, 2, 'APPROVED'),
                                                                     (1, 3, 'APPROVED'),
                                                                     (5, 4, 'PENDING');

-- 插入积分商城物品数据
INSERT INTO `store_items` (`name`, `description`, `cost`, `stock`, `image_url`) VALUES
                                                                                    ('环保购物袋', '可重复使用的环保购物袋', 50, 100, 'bag.jpg'),
                                                                                    ('志愿者T恤', '专属志愿者T恤', 100, 50, 'tshirt.jpg'),
                                                                                    ('保温杯', '不锈钢保温杯', 150, 30, 'cup.jpg'),
                                                                                    ('笔记本', '精美笔记本', 30, 200, 'notebook.jpg'),
                                                                                    ('雨伞', '折叠雨伞', 80, 40, 'umbrella.jpg');

-- 插入积分兑换记录数据
INSERT INTO `exchange_logs` (`user_id`, `item_id`, `points_spent`) VALUES
                                                                       (1, 1, 50),
                                                                       (2, 3, 150),
                                                                       (3, 2, 100),
                                                                       (1, 4, 30),
                                                                       (5, 5, 80);

INSERT INTO `activities` (
    `title`,
    `description`,
    `location`,
    `start_time`,
    `end_time`,
    `required_volunteers`,
    `organizer_id`
) VALUES
      (
          '关爱空巢老人志愿服务',
          '陪伴社区独居老人，为他们带去温暖和关怀，帮助解决日常生活中的困难',
          '阳光社区',
          '2024-01-15 09:00:00',
          '2024-01-15 12:00:00',
          20,
          NULL
      ),
      (
          '环保清洁行动',
          '保护环境从我做起，清理公园垃圾，宣传环保理念',
          '滨江公园',
          '2024-01-18 09:00:00',
          '2024-01-18 13:00:00',
          30,
          NULL
      ),
      (
          '儿童阅读推广活动',
          '为山区儿童带去知识的力量和阅读的快乐',
          '希望小学',
          '2024-01-20 10:00:00',
          '2024-01-20 15:00:00',
          15,
          NULL
      ),
      (
          '社区义诊服务',
          '为社区居民提供免费健康检查和医疗咨询',
          '和谐社区',
          '2024-01-22 08:00:00',
          '2024-01-22 14:00:00',
          25,
          NULL
      ),
      (
          '线上编程教学',
          '为偏远地区学生提供免费的编程启蒙教育',
          '在线',
          '2024-01-25 14:00:00',
          '2024-01-25 16:00:00',
          50,
          NULL
      ),
      (
          '植树造林活动',
          '种下希望的种子，为地球增添绿色',
          '郊区',
          '2024-01-12 08:00:00',
          '2024-01-12 16:00:00',
          50,
          NULL
      );

INSERT INTO `store_items` (`name`, `description`, `cost`, `stock`, `image_url`) VALUES
                                                                                    ('蓝牙无线耳机', '高品质音效，长续航，运动防汗', 800, 15, 'https://plus.unsplash.com/premium_photo-1680346529370-c940bd7536ad?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                    ('精装励志图书套装', '经典励志书籍，提升个人修养', 300, 25, 'https://plus.unsplash.com/premium_photo-1734709479958-dbee57558557?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                    ('环保购物袋套装', '可重复使用，环保材质，多种尺寸', 150, 50, 'https://images.unsplash.com/photo-1657593322459-a0cb1ff1902d?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                    ('有机蜂蜜礼盒', '纯天然有机蜂蜜，营养丰富', 400, 8, 'https://images.unsplash.com/photo-1587468362560-e64c0ede4c5a?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                    ('智能手环', '健康监测，运动追踪，消息提醒', 600, 20, 'https://images.unsplash.com/photo-1586484716673-9ba792bf9dbe?q=80&w=686&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                    ('为山区儿童捐赠文具', '为贫困山区的孩子们送去学习用品', 200, 999, 'https://plus.unsplash.com/premium_photo-1664110691177-f9c861df3703?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                    ('便携式充电宝', '大容量，快充技术，多设备兼容', 500, 12, 'https://plus.unsplash.com/premium_photo-1679423807759-afb3c145c535?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
                                                                                    ('创意笔记本套装', '精美设计，优质纸张，激发创作灵感', 180, 35, 'https://plus.unsplash.com/premium_photo-1685136482508-ff00024d187a?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D');


