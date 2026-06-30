-- 插入测试数据

-- 测试用户（密码：user123）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', 0, 1);

-- 测试学生信息
INSERT INTO `student_info` (`user_id`, `student_card_id`, `school`, `major`, `grade`, `class_name`) VALUES
(2, '2021001001', '清华大学', '计算机科学', '大三', '计算机2101班');

-- 分类数据
INSERT INTO `category` (`name`, `parent_id`, `sort`, `status`) VALUES
('手机数码', 0, 1, 1),
('电脑办公', 0, 2, 1),
('家用电器', 0, 3, 1),
('服饰鞋包', 0, 4, 1),
('食品饮料', 0, 5, 1);

-- 手机数码子分类
INSERT INTO `category` (`name`, `parent_id`, `sort`, `status`) VALUES
('手机', 1, 1, 1),
('平板', 1, 2, 1),
('耳机', 1, 3, 1),
('智能手表', 1, 4, 1);

-- 电脑办公子分类
INSERT INTO `category` (`name`, `parent_id`, `sort`, `status`) VALUES
('笔记本', 2, 1, 1),
('台式机', 2, 2, 1),
('显示器', 2, 3, 1),
('键盘鼠标', 2, 4, 1);

-- 商品数据
INSERT INTO `product` (`name`, `description`, `price`, `original_price`, `stock`, `sales`, `category_id`, `main_image`, `status`) VALUES
('iPhone 15 Pro Max', '苹果最新旗舰手机，A17 Pro芯片', 9999.00, 10999.00, 100, 50, 6, 'https://via.placeholder.com/300', 1),
('MacBook Pro 14寸', 'M3 Pro芯片，专业级笔记本', 16999.00, 17999.00, 50, 30, 10, 'https://via.placeholder.com/300', 1),
('AirPods Pro 2', '主动降噪蓝牙耳机', 1799.00, 1999.00, 200, 150, 8, 'https://via.placeholder.com/300', 1),
('Apple Watch Series 9', '智能健康手表', 2999.00, 3299.00, 150, 80, 9, 'https://via.placeholder.com/300', 1),
('小米14', '骁龙8 Gen3旗舰手机', 4999.00, 5299.00, 200, 120, 6, 'https://via.placeholder.com/300', 1),
('联想小新Pro 16', '16英寸轻薄笔记本', 5999.00, 6499.00, 100, 60, 10, 'https://via.placeholder.com/300', 1),
('华为FreeBuds Pro 3', '降噪蓝牙耳机', 1499.00, 1699.00, 180, 100, 8, 'https://via.placeholder.com/300', 1),
('Redmi Buds 4 Pro', '高性价比降噪耳机', 399.00, 499.00, 500, 300, 8, 'https://via.placeholder.com/300', 1),
('戴森V15 Detect', '智能无绳吸尘器', 4990.00, 5490.00, 80, 40, 17, 'https://via.placeholder.com/300', 1),
('美的空调', '1.5匹变频空调', 3299.00, 3699.00, 100, 70, 17, 'https://via.placeholder.com/300', 1);

-- 测试收货地址
INSERT INTO `address` (`user_id`, `name`, `phone`, `province`, `city`, `district`, `detail`, `is_default`) VALUES
(2, '张三', '13800138000', '北京市', '北京市', '朝阳区', '某某街道123号', 1);
