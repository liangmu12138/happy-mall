package com.happy.mall.controller.admin;

import com.happy.mall.common.Result;
import com.happy.mall.entity.User;
import com.happy.mall.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员管理控制器
 */
@Tag(name = "管理员管理接口")
@RestController
@RequestMapping("/api/admin/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @Operation(summary = "获取所有管理员")
    @GetMapping("/list")
    public Result<?> getAdminList() {
        List<User> admins = userService.getAllAdmins();
        return Result.success(admins);
    }

    @Operation(summary = "创建管理员账号")
    @PostMapping("/create")
    public Result<?> createAdmin(Authentication authentication, @RequestBody Map<String, String> params) {
        User currentUser = (User) authentication.getPrincipal();
        String username = params.get("username");
        String password = params.get("password");
        String nickname = params.get("nickname");

        userService.createAdmin(username, password, nickname, currentUser.getId());
        return Result.success("管理员创建成功");
    }

    @Operation(summary = "修改用户角色")
    @PutMapping("/role")
    public Result<?> updateUserRole(Authentication authentication, @RequestBody Map<String, Object> params) {
        User currentUser = (User) authentication.getPrincipal();
        Long userId = Long.parseLong(params.get("userId").toString());
        Integer role = Integer.parseInt(params.get("role").toString());

        userService.updateUserRole(userId, role, currentUser.getId());
        return Result.success("角色更新成功");
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/status")
    public Result<?> updateUserStatus(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("id").toString());
        Integer status = Integer.parseInt(params.get("status").toString());

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        userService.updateById(user);
        return Result.success("操作成功");
    }
}
