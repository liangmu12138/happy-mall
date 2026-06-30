package com.happy.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 学习资料实体
 */
@Data
@TableName("study_material")
public class StudyMaterial extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 上传用户ID
     */
    private Long userId;

    /**
     * 资料标题
     */
    private String title;

    /**
     * 资料描述
     */
    private String description;

    /**
     * 分类：教材/笔记/考研/考级/课件/其他
     */
    private String category;

    /**
     * 学科/课程
     */
    private String subject;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 封面图片
     */
    private String images;

    /**
     * 价格（0为免费）
     */
    private BigDecimal price;

    /**
     * 下载次数
     */
    private Integer downloadCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;
}
