package com.happy.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 学习搭子实体
 */
@Data
@TableName("study_buddy")
public class StudyBuddy extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发起用户ID
     */
    private Long userId;

    /**
     * 搭子类型：自习/考研/四六级/编程/其他
     */
    private String buddyType;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 目标
     */
    private String target;

    /**
     * 地点
     */
    private String location;

    /**
     * 时间信息
     */
    private String timeInfo;

    /**
     * 最多人数
     */
    private Integer maxMembers;

    /**
     * 当前人数
     */
    private Integer currentMembers;

    /**
     * 状态
     */
    private Integer status;
}
