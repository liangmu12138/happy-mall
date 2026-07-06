package com.happy.mall.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.happy.mall.common.Result;
import com.happy.mall.entity.User;
import com.happy.mall.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 用户控制器
 */
@Tag(name = "管理端-用户接口")
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "分页查询用户")
    @GetMapping("/list")
    public Result<?> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getPhone, keyword);
        }

        wrapper.orderByDesc(User::getCreateTime);
        Page<User> page = userService.page(new Page<>(pageNum, pageSize), wrapper);

        // 隐藏密码
        page.getRecords().forEach(user -> user.setPassword(null));

        return Result.success(page);
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<?> getUserDetail(@PathVariable Long id) {
        User user = userService.getUserInfo(id);
        return Result.success(user);
    }

    @Operation(summary = "更新用户状态")
    @PutMapping("/status")
    public Result<?> updateUserStatus(@RequestBody java.util.Map<String, Object> params) {
        Long id = Long.parseLong(params.get("id").toString());
        Integer status = Integer.parseInt(params.get("status").toString());
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        userService.updateById(user);
        return Result.success("操作成功");
    }

    @Operation(summary = "删除用户（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getRole() == 1) {
            return Result.error("不能删除管理员账号");
        }
        userService.removeById(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "获取用户统计")
    @GetMapping("/statistics")
    public Result<?> getUserStatistics() {
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalUsers", userService.count());
        statistics.put("todayNewUsers", userService.count(new LambdaQueryWrapper<User>()
                .ge(User::getCreateTime, java.time.LocalDateTime.now().withHour(0).withMinute(0).withSecond(0))));
        return Result.success(statistics);
    }
}
