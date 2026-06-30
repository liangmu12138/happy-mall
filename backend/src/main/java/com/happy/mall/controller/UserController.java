package com.happy.mall.controller;

import com.happy.mall.common.Result;
import com.happy.mall.entity.User;
import com.happy.mall.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String nickname = params.get("nickname");
        String studentCardId = params.get("studentCardId");
        String school = params.get("school");
        String major = params.get("major");
        String grade = params.get("grade");
        String className = params.get("className");

        Map<String, Object> result = userService.register(username, password, nickname,
                studentCardId, school, major, grade, className);
        return Result.success("注册成功", result);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        Map<String, Object> result = userService.login(username, password);
        return Result.success("登录成功", result);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<?> getUserInfo(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        User userInfo = userService.getUserInfo(user.getId());
        return Result.success(userInfo);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<?> updateUserInfo(Authentication authentication, @RequestBody Map<String, Object> params) {
        User user = (User) authentication.getPrincipal();
        String nickname = params.get("nickname") != null ? params.get("nickname").toString() : null;
        String avatar = params.get("avatar") != null ? params.get("avatar").toString() : null;
        String email = params.get("email") != null ? params.get("email").toString() : null;
        String phone = params.get("phone") != null ? params.get("phone").toString() : null;
        Integer gender = params.get("gender") != null ? Integer.parseInt(params.get("gender").toString()) : null;

        userService.updateUserInfo(user.getId(), nickname, avatar, email, phone, gender);
        return Result.success("更新成功");
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<?> updatePassword(Authentication authentication, @RequestBody Map<String, String> params) {
        User user = (User) authentication.getPrincipal();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.updatePassword(user.getId(), oldPassword, newPassword);
        return Result.success("密码修改成功");
    }
}
