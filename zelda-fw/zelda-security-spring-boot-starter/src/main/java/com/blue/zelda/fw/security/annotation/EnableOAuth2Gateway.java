package com.blue.zelda.fw.security.annotation;

import com.blue.zelda.fw.security.autoconfigure.OAuth2GatewayAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 OAuth2 网关安全功能
 *
 * <p>使用此注解可以激活 Spring Cloud Gateway 的 OAuth2 认证功能，
 * 包括请求拦截、用户信息透传、Token 校验等安全机制。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(OAuth2GatewayAutoConfiguration.class)
public @interface EnableOAuth2Gateway {
}