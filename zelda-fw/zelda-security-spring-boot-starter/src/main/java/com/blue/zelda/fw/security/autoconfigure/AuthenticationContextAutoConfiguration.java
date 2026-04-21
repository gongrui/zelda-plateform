package com.blue.zelda.fw.security.autoconfigure;

import com.blue.zelda.fw.security.config.AuthenticationProperties;
import com.blue.zelda.fw.security.context.AuthenticationDetails;
import com.blue.zelda.fw.security.holder.AuthenticationContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 认证上下文自动配置
 *
 * <p>当引入 zelda-security-spring-boot-starter 并启用认证上下文时，自动配置：</p>
 * <ul>
 *   <li>AuthenticationContextHolder - 用户上下文持有器</li>
 *   <li>AuthenticationContextFilter - 从 Header 提取用户信息的过滤器</li>
 * </ul>
 *
 * <h3>使用要求：</h3>
 * <ol>
 *   <li>引入依赖：
 *   <pre>{@code
 *   <dependency>
 *       <groupId>com.blue</groupId>
 *       <artifactId>zelda-security-spring-boot-starter</artifactId>
 *   </dependency>
 *   }</pre>
 *   </li>
 *   <li>Gateway 透传用户信息 Header：
 *   <pre>{@code
 *   // 请求头添加用户信息
 *   X-User-Id: 12345
 *   X-Tenant-Code: T001
 *   X-Nickname: 张三
 *   }</pre>
 *   </li>
 *   <li>业务代码获取用户信息：
 *   <pre>{@code
 *   Long userId = AuthenticationContextHolder.userId();
 *   String tenantCode = AuthenticationContextHolder.tenantCode();
 *   }</pre>
 *   </li>
 * </ol>
 *
 * <h3>配置选项：</h3>
 * <pre>{@code
 * zelda:
 *   security:
 *     authentication:
 *       enabled: true
 *       user-id-header: X-User-Id
 *       tenant-code-header: X-Tenant-Code
 *       ignore-paths:
 *         - /actuator/**
 *         - /public/**
 * }</pre>
 *
 * @author gongrui
 * @since 1.0.0
 * @see AuthenticationContextHolder
 * @see AuthenticationDetails
 */
@AutoConfiguration
@EnableConfigurationProperties(AuthenticationProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "jakarta.servlet.Filter")
@ConditionalOnProperty(prefix = "zelda.security.authentication", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class AuthenticationContextAutoConfiguration {

    private final AuthenticationProperties properties;

//    /**
//     * 注册认证上下文过滤器
//     */
//    @Bean
//    @ConditionalOnMissingBean
//    public AuthenticationContextFilter authenticationContextFilter() {
//        return new AuthenticationContextFilter(properties);
//    }
}
