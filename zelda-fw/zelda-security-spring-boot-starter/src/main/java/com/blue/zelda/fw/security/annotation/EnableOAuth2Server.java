package com.blue.zelda.fw.security.annotation;

import com.blue.zelda.fw.security.autoconfigure.OAuth2ServerAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 OAuth2 授权服务器功能
 *
 * <p>使用此注解可以激活 OAuth2 授权服务器功能，
 * 支持授权码、密码模式、客户端凭证等多种授权模式，
 * 提供 Token 颁发、刷新、校验等核心功能。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(OAuth2ServerAutoConfiguration.class)
public @interface EnableOAuth2Server {

}