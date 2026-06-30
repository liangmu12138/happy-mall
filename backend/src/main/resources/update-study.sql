-- 学习模块数据库更新脚本
-- 执行此脚本添加学习模块相关表

USE happy_mall;

-- 学习资料表
CREATE TABLE IF NOT EXISTS `study_material` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '上传用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '资料标题',
    `description` TEXT COMMENT '资料描述',
    `category` VARCHAR(50) NOT NULL COMMENT '教材/笔记/考研/考级/课件/其他',
    `subject` VARCHAR(100) COMMENT '学科/课程',
    `file_url` VARCHAR(500) COMMENT '文件地址',
    `images` TEXT COMMENT '封面图片',
    `price` DECIMAL(10,2) DEFAULT 0 COMMENT '价格（0为免费）',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `rating` DECIMAL(3,2) DEFAULT 0 COMMENT '评分',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资料表';

-- 课程评价表（学长学姐说）
CREATE TABLE IF NOT EXISTS `course_review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '评价用户ID',
    `course_name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `teacher_name` VARCHAR(50) COMMENT '教师姓名',
    `school` VARCHAR(100) COMMENT '学校',
    `rating` INT NOT NULL COMMENT '评分1-5',
    `difficulty` INT COMMENT '难度1-5',
    `exam_difficulty` INT COMMENT '考试难度1-5',
    `grade_score` INT COMMENT '给分情况1-5',
    `content` TEXT COMMENT '评价内容',
    `tips` TEXT COMMENT '学习建议',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_course_name` (`course_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评价表';

-- 学习搭子表
CREATE TABLE IF NOT EXISTS `study_buddy` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '发起用户ID',
    `buddy_type` VARCHAR(50) NOT NULL COMMENT '自习/考研/四六级/编程/其他',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `description` TEXT COMMENT '描述',
    `target` VARCHAR(100) COMMENT '目标',
    `location` VARCHAR(200) COMMENT '地点',
    `time_info` VARCHAR(100) COMMENT '时间信息',
    `max_members` INT DEFAULT 1 COMMENT '最多人数',
    `current_members` INT DEFAULT 1 COMMENT '当前人数',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted` TINYINT DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_buddy_type` (`buddy_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习搭子表';

-- 学习搭子参与表
CREATE TABLE IF NOT EXISTS `study_buddy_join` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `buddy_id` BIGINT NOT NULL COMMENT '搭子ID',
    `user_id` BIGINT NOT NULL COMMENT '参与用户ID',
    `status` TINYINT DEFAULT 0 COMMENT '0-待确认 1-已确认',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_buddy_id` (`buddy_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习搭子参与表';

SELECT '✅ 学习模块数据库更新完成！' AS '结果';
