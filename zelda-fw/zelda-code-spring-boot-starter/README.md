# Zelda Code Spring Boot Starter
## 遵循 Spring Boot 4 官方规范

## 依赖
- zelda-core（全局基座）
- Redis

## 编号格式
[2位前缀] + [8位日期] + [2位节点] + [4位流水] + [1位校验位]
示例：DD202604050100457

## 使用
1. 引入依赖
<dependency>
    <groupId>com.blue.zelda</groupId>
    <artifactId>zelda-code-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>

2. 配置 Redis
spring:
  data:
    redis:
      host: localhost
      port: 6379

3. 使用
private final SmartCodeGenerator codeGenerator;

// 生成
codeGenerator.generate("DD");

// 校验
codeGenerator.verify("DD202604050100457");
