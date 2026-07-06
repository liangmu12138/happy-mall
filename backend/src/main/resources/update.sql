-- 数据库更新脚本
-- 如果你已经初始化过数据库，执行这个脚本来添加新功能

USE happy_mall;

-- 检查并添加学生信息表
CREATE TABLE IF NOT EXISTS `student_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `student_card_id` VARCHAR(50) NOT NULL COMMENT '学生卡ID',
    `school` VARCHAR(100) DEFAULT NULL COMMENT '学校名称',
    `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
    `grade` VARCHAR(20) DEFAULT NULL COMMENT '年级',
    `class_name` VARCHAR(50) DEFAULT NULL COMMENT '班级',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_card_id` (`student_card_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 检查并添加管理员操作日志表
CREATE TABLE IF NOT EXISTS `admin_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `admin_id` BIGINT NOT NULL COMMENT '管理员ID',
    `action` VARCHAR(100) NOT NULL COMMENT '操作类型',
    `target_type` VARCHAR(50) COMMENT '操作对象类型',
    `target_id` BIGINT COMMENT '操作对象ID',
    `detail` TEXT COMMENT '操作详情',
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作日志表';

-- 管理员账号会在应用启动时自动创建
-- 账号：admin / admin123

SELECT '✅ 数据库更新完成！' AS '结果';
