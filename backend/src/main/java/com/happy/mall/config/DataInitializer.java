package com.happy.mall.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.happy.mall.entity.User;
import com.happy.mall.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 * 应用启动时自动创建超级管理员账号
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 检查超级管理员是否存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "Bzh");
        User admin = userMapper.selectOne(wrapper);

        if (admin == null) {
            // 创建超级管理员
            admin = new User();
            admin.setUsername("Bzh");
            admin.setPassword(passwordEncoder.encode("Bzh123456#"));
            admin.setNickname("超级管理员");
            admin.setRole(1); // 管理员
            admin.setStatus(1); // 启用
            admin.setGender(0);
            userMapper.insert(admin);
            log.info("✅ 超级管理员账号创建成功: Bzh / Bzh123456#");
        } else {
            log.info("✅ 超级管理员账号已存在");
        }
    }
}
