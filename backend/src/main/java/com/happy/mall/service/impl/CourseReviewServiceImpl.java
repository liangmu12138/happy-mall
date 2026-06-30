package com.happy.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.happy.mall.common.BusinessException;
import com.happy.mall.entity.CourseReview;
import com.happy.mall.mapper.CourseReviewMapper;
import com.happy.mall.service.CourseReviewService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 课程评价 Service 实现类
 */
@Service
public class CourseReviewServiceImpl extends ServiceImpl<CourseReviewMapper, CourseReview> implements CourseReviewService {

    @Override
    public Page<CourseReview> getReviewPage(Integer pageNum, Integer pageSize, String courseName, String school) {
        LambdaQueryWrapper<CourseReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseReview::getStatus, 1);

        if (StringUtils.hasText(courseName)) {
            wrapper.like(CourseReview::getCourseName, courseName);
        }

        if (StringUtils.hasText(school)) {
            wrapper.like(CourseReview::getSchool, school);
        }

        wrapper.orderByDesc(CourseReview::getCreateTime);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public CourseReview getReviewDetail(Long id) {
        CourseReview review = this.getById(id);
        if (review == null || review.getStatus() != 1) {
            throw new BusinessException("评价不存在或已删除");
        }
        return review;
    }

    @Override
    public void addReview(CourseReview review) {
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        if (review.getLikeCount() == null) review.setLikeCount(0);
        if (review.getStatus() == null) review.setStatus(1);
        this.save(review);
    }

    @Override
    public void updateReview(CourseReview review) {
        if (review.getId() == null) {
            throw new BusinessException("评价ID不能为空");
        }
        this.updateById(review);
    }

    @Override
    public void deleteReview(Long id, Long userId) {
        CourseReview review = this.getById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的评价");
        }
        this.removeById(id);
    }

    @Override
    public void likeReview(Long id) {
        CourseReview review = this.getById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        review.setLikeCount(review.getLikeCount() + 1);
        this.updateById(review);
    }
}
