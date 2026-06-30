package com.happy.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 学生信息实体
 */
@Data
@TableName("student_info")
public class StudentInfo extends BaseEntity {

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 学生卡ID
     */
    private String studentCardId;

    /**
     * 学校名称
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 年级
     */
    private String grade;

    /**
     * 班级
     */
    private String className;
}
