package com.happy.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 课程评价实体（学长学姐说）
 */
@Data
@TableName("course_review")
public class CourseReview extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 评价用户ID
     */
    private Long userId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 学校
     */
    private String school;

    /**
     * 评分1-5
     */
    private Integer rating;

    /**
     * 难度1-5
     */
    private Integer difficulty;

    /**
     * 考试难度1-5
     */
    private Integer examDifficulty;

    /**
     * 给分情况1-5
     */
    private Integer gradeScore;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 学习建议
     */
    private String tips;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 状态
     */
    private Integer status;
}
