package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.CourseReview;

/**
 * 课程评价 Service
 */
public interface CourseReviewService extends IService<CourseReview> {

    /**
     * 分页查询课程评价
     */
    Page<CourseReview> getReviewPage(Integer pageNum, Integer pageSize, String courseName, String school);

    /**
     * 获取评价详情
     */
    CourseReview getReviewDetail(Long id);

    /**
     * 发表评价
     */
    void addReview(CourseReview review);

    /**
     * 更新评价
     */
    void updateReview(CourseReview review);

    /**
     * 删除评价
     */
    void deleteReview(Long id, Long userId);

    /**
     * 点赞评价
     */
    void likeReview(Long id);
}
