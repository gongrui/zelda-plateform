package com.blue.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Demo 应用启动类
 * 用于测试 Sa-Token OAuth2 注解功能
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.blue.demo.mapper")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
