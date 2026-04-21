package com.blue.zelda.fw.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 认证上下文配置属性
 *
 * <p>配置下游服务如何从请求中获取用户认证信息</p>
 *
 * <p>配置示例：</p>
 * <pre>
 * zelda:
 *   security:
 *     authentication:
 *       enabled: true
 *       user-id-header: X-User-Id
 *       tenant-code-header: X-Tenant-Code
 *       token-header: Authorization
 *       user-info-cache:
 *         enabled: true
 *         ttl: 3600
 * </pre>
 *
 * @author gongrui
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "zelda.security.authentication")
public class AuthenticationProperties {

    /**
     * 是否启用认证上下文自动注入
     */
    private boolean enabled = true;

    /**
     * 用户ID请求头名称
     */
    private String userIdHeader = "X-User-Id";

    /**
     * 租户编码请求头名称
     */
    private String tenantCodeHeader = "X-Tenant-Code";

    /**
     * 用户类型请求头名称
     */
    private String userTypeHeader = "X-User-Type";

    /**
     * 用户昵称请求头名称
     */
    private String nicknameHeader = "X-Nickname";

    /**
     * 手机号请求头名称
     */
    private String mobileHeader = "X-Mobile";

    /**
     * 客户端ID请求头名称
     */
    private String clientIdHeader = "X-Client-Id";

    /**
     * 忽略认证的路径列表
     */
    private List<String> ignorePaths;

    /**
     * 用户信息缓存配置
     */
    private UserInfoCache userInfoCache = new UserInfoCache();

    /**
     * 用户信息缓存配置
     */
    @Data
    public static class UserInfoCache {
        /**
         * 是否启用缓存
         */
        private boolean enabled = false;

        /**
         * 缓存过期时间（秒）
         */
        private long ttl = 3600;
    }
}
