package com.happy.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.happy.mall.common.Result;
import com.happy.mall.entity.CourseReview;
import com.happy.mall.entity.StudyBuddy;
import com.happy.mall.entity.StudyMaterial;
import com.happy.mall.entity.User;
import com.happy.mall.service.CourseReviewService;
import com.happy.mall.service.StudyBuddyService;
import com.happy.mall.service.StudyMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学习模块控制器
 */
@Tag(name = "学习模块接口")
@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyMaterialService materialService;
    private final CourseReviewService reviewService;
    private final StudyBuddyService buddyService;

    // ==================== 资料相关 ====================

    @Operation(summary = "获取资料列表")
    @GetMapping("/material/list")
    public Result<?> getMaterialList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        Page<StudyMaterial> page = materialService.getMaterialPage(pageNum, pageSize, category, keyword);
        return Result.success(page);
    }

    @Operation(summary = "获取资料详情")
    @GetMapping("/material/{id}")
    public Result<?> getMaterialDetail(@PathVariable Long id) {
        StudyMaterial material = materialService.getMaterialDetail(id);
        return Result.success(material);
    }

    @Operation(summary = "上传资料")
    @PostMapping("/material/upload")
    public Result<?> uploadMaterial(Authentication authentication, @RequestBody StudyMaterial material) {
        User user = (User) authentication.getPrincipal();
        material.setUserId(user.getId());
        materialService.uploadMaterial(material);
        return Result.success("上传成功");
    }

    @Operation(summary = "更新资料")
    @PutMapping("/material/update")
    public Result<?> updateMaterial(Authentication authentication, @RequestBody StudyMaterial material) {
        User user = (User) authentication.getPrincipal();
        materialService.updateMaterial(material);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除资料")
    @DeleteMapping("/material/{id}")
    public Result<?> deleteMaterial(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        materialService.deleteMaterial(id, user.getId());
        return Result.success("删除成功");
    }

    @Operation(summary = "点赞资料")
    @PostMapping("/material/{id}/like")
    public Result<?> likeMaterial(@PathVariable Long id) {
        materialService.likeMaterial(id);
        return Result.success("点赞成功");
    }

    @Operation(summary = "下载资料")
    @PostMapping("/material/{id}/download")
    public Result<?> downloadMaterial(@PathVariable Long id) {
        materialService.downloadMaterial(id);
        return Result.success("下载成功");
    }

    // ==================== 课程评价相关 ====================

    @Operation(summary = "获取课程评价列表")
    @GetMapping("/review/list")
    public Result<?> getReviewList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String school) {
        Page<CourseReview> page = reviewService.getReviewPage(pageNum, pageSize, courseName, school);
        return Result.success(page);
    }

    @Operation(summary = "获取评价详情")
    @GetMapping("/review/{id}")
    public Result<?> getReviewDetail(@PathVariable Long id) {
        CourseReview review = reviewService.getReviewDetail(id);
        return Result.success(review);
    }

    @Operation(summary = "发表评价")
    @PostMapping("/review/add")
    public Result<?> addReview(Authentication authentication, @RequestBody CourseReview review) {
        User user = (User) authentication.getPrincipal();
        review.setUserId(user.getId());
        reviewService.addReview(review);
        return Result.success("发表成功");
    }

    @Operation(summary = "更新评价")
    @PutMapping("/review/update")
    public Result<?> updateReview(Authentication authentication, @RequestBody CourseReview review) {
        User user = (User) authentication.getPrincipal();
        reviewService.updateReview(review);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除评价")
    @DeleteMapping("/review/{id}")
    public Result<?> deleteReview(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        reviewService.deleteReview(id, user.getId());
        return Result.success("删除成功");
    }

    @Operation(summary = "点赞评价")
    @PostMapping("/review/{id}/like")
    public Result<?> likeReview(@PathVariable Long id) {
        reviewService.likeReview(id);
        return Result.success("点赞成功");
    }

    // ==================== 学习搭子相关 ====================

    @Operation(summary = "获取学习搭子列表")
    @GetMapping("/buddy/list")
    public Result<?> getBuddyList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String buddyType,
            @RequestParam(required = false) String keyword) {
        Page<Map<String, Object>> page = buddyService.getBuddyPage(pageNum, pageSize, buddyType, keyword);
        return Result.success(page);
    }

    @Operation(summary = "获取搭子详情")
    @GetMapping("/buddy/{id}")
    public Result<?> getBuddyDetail(@PathVariable Long id) {
        Map<String, Object> result = buddyService.getBuddyDetail(id);
        return Result.success(result);
    }

    @Operation(summary = "发布学习搭子")
    @PostMapping("/buddy/create")
    public Result<?> createBuddy(Authentication authentication, @RequestBody StudyBuddy buddy) {
        User user = (User) authentication.getPrincipal();
        buddy.setUserId(user.getId());
        buddyService.createBuddy(buddy);
        return Result.success("发布成功");
    }

    @Operation(summary = "更新学习搭子")
    @PutMapping("/buddy/update")
    public Result<?> updateBuddy(Authentication authentication, @RequestBody StudyBuddy buddy) {
        User user = (User) authentication.getPrincipal();
        buddyService.updateBuddy(buddy);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除学习搭子")
    @DeleteMapping("/buddy/{id}")
    public Result<?> deleteBuddy(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        buddyService.deleteBuddy(id, user.getId());
        return Result.success("删除成功");
    }

    @Operation(summary = "加入学习搭子")
    @PostMapping("/buddy/{id}/join")
    public Result<?> joinBuddy(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        buddyService.joinBuddy(id, user.getId());
        return Result.success("加入成功");
    }

    @Operation(summary = "退出学习搭子")
    @PostMapping("/buddy/{id}/leave")
    public Result<?> leaveBuddy(Authentication authentication, @PathVariable Long id) {
        User user = (User) authentication.getPrincipal();
        buddyService.leaveBuddy(id, user.getId());
        return Result.success("退出成功");
    }
}
