package com.happy.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Happy Mall 电商平台
 */
@SpringBootApplication
@MapperScan("com.happy.mall.mapper")
public class HappyMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(HappyMallApplication.class, args);
    }
}
