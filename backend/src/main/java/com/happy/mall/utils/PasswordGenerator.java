package com.happy.mall.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具
 * 用于生成 BCrypt 加密密码
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 生成 Bzh123456# 的加密密码
        String rawPassword = "Bzh123456#";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("密码: " + rawPassword);
        System.out.println("加密后: " + encodedPassword);

        // 生成 user123 的加密密码
        String rawPassword2 = "user123";
        String encodedPassword2 = encoder.encode(rawPassword2);
        System.out.println("\n密码: " + rawPassword2);
        System.out.println("加密后: " + encodedPassword2);
    }
}
