package com.happy.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习搭子参与实体
 */
@Data
@TableName("study_buddy_join")
public class StudyBuddyJoin {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 搭子ID
     */
    private Long buddyId;

    /**
     * 参与用户ID
     */
    private Long userId;

    /**
     * 状态：0-待确认，1-已确认
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
