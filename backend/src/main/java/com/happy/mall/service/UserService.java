package com.happy.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.happy.mall.entity.User;

import java.util.Map;

/**
 * 用户 Service
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册（包含学生卡信息）
     */
    Map<String, Object> register(String username, String password, String nickname,
                                  String studentCardId, String school, String major,
                                  String grade, String className);

    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password);

    /**
     * 获取用户信息
     */
    User getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, String nickname, String avatar, String email, String phone, Integer gender);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 创建管理员账号
     */
    void createAdmin(String username, String password, String nickname, Long creatorId);

    /**
     * 更新用户角色
     */
    void updateUserRole(Long userId, Integer role, Long operatorId);

    /**
     * 获取所有管理员
     */
    java.util.List<User> getAllAdmins();
}
