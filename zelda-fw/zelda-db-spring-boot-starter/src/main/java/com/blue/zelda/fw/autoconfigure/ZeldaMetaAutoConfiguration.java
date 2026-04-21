package com.blue.zelda.fw.autoconfigure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.web.bind.annotation.RestController;

/**
 * 元数据模块主自动配置类
 *
 * <p>启用元数据功能，包括：</p>
 * <ul>
 *   <li>扫描 MyBatis Mapper 接口</li>
 *   <li>扫描 Spring 组件（Controller、Service、Component 等）</li>
 * </ul>
 *
 * <p>使用条件：</p>
 * <ul>
 *   <li>仅在 Web 应用环境中生效</li>
 *   <li>类路径中存在 {@link RestController}</li>
 * </ul>
 *
 * @author zelda
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication // 仅在 Web 环境生效
@ConditionalOnClass(RestController.class)
@MapperScan("com.blue.zelda.fw.meta.**.mapper")
@ComponentScan("com.blue.zelda.fw.meta")
public class ZeldaMetaAutoConfiguration {


}
