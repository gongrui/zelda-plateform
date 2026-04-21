package com.blue.zelda.fw.security.annotation;

import com.blue.zelda.fw.security.autoconfigure.OAuth2ResourceAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 OAuth2 资源服务器安全功能
 *
 * <p>使用此注解可以激活 Spring Boot 应用的 OAuth2 资源服务器功能，
 * 包括请求拦截器、Token 校验、权限控制等安全机制。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(OAuth2ResourceAutoConfiguration.class)
public @interface EnableOAuth2Resource {
}