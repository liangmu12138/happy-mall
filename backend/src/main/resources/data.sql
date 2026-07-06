-- 插入测试数据

-- 测试用户（学生卡ID: testuser, 密码: user123）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试同学', 0, 1);

-- 测试学生信息
INSERT INTO `student_info` (`user_id`, `student_card_id`) VALUES
(2, '2021001001');

-- 学习资料分类数据
INSERT INTO `category` (`name`, `parent_id`, `sort`, `status`) VALUES
('教材', 0, 1, 1),
('笔记', 0, 2, 1),
('考研', 0, 3, 1),
('考级', 0, 4, 1),
('课件', 0, 5, 1),
('其他', 0, 6, 1);

-- 测试学习资料
INSERT INTO `study_material` (`user_id`, `title`, `description`, `category`, `subject`, `price`, `download_count`, `like_count`, `status`) VALUES
(2, '高等数学上册笔记', '详细整理了高数上的重点知识，适合期末复习', '笔记', '高等数学', 0, 128, 45, 1),
(2, '线性代数习题集', '包含历年考试真题和详解', '教材', '线性代数', 5.00, 89, 32, 1),
(2, '大学英语四级词汇', '核心词汇整理，带例句', '考级', '大学英语', 0, 256, 78, 1),
(2, '数据结构课件', '老师上课用的PPT，包含所有章节', '课件', '数据结构', 0, 167, 56, 1),
(2, '考研数学真题解析', '近十年考研数学真题详细解析', '考研', '数学', 9.90, 312, 98, 1);

-- 测试课程评价
INSERT INTO `course_review` (`user_id`, `course_name`, `teacher_name`, `rating`, `difficulty`, `exam_difficulty`, `grade_score`, `content`, `tips`, `like_count`, `status`) VALUES
(2, '高等数学A', '张老师', 5, 4, 3, 5, '张老师讲课非常清晰，重难点突出，给分也很友好', '上课认真听讲，课后多做习题', 23, 1),
(2, '线性代数', '李老师', 4, 3, 3, 4, '老师很负责，但是内容比较抽象需要多理解', '建议配合视频教程学习', 15, 1),
(2, '大学英语', '王老师', 4, 2, 2, 5, '课堂氛围很好，作业不多，给分不错', '平时注意积累单词', 18, 1);

-- 测试学习搭子
INSERT INTO `study_buddy` (`user_id`, `buddy_type`, `title`, `description`, `target`, `location`, `time_info`, `max_members`, `current_members`, `status`) VALUES
(2, '自习', '期末复习自习搭子', '一起复习高数和线代，互相监督', '期末考试全科通过', '图书馆三楼', '每天晚上7-10点', 4, 1, 1),
(2, '考研', '25考研数学组队', '找一起考研的小伙伴，共享资料，互相鼓励', '考研上岸', '自习室', '每天上午9-12点', 3, 1, 1),
(2, '四六级', '四级备考小组', '一起背单词、练听力，打卡学习', '四级550+', '线上+线下', '每天晚上8-9点', 5, 1, 1);
