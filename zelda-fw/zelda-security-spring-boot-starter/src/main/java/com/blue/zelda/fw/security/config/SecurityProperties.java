package com.blue.zelda.fw.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全配置属性类
 *
 * <p>通过配置文件（application.yml 或 application.properties）中的 {@code zelda.security} 前缀
 * 来配置各种安全相关的参数，包括服务器配置、资源服务器配置和网关配置。</p>
 *
 * <p>配置示例：</p>
 * <pre>
 * zelda:
 *   security:
 *     server:
 *       code-timeout: 300
 *       access-token-timeout: 7200
 *       refresh-token-timeout: 2592000
 *     resource:
 *       enabled: true
 *       ignore-paths:
 *         - /actuator/**
 *     gateway:
 *       enabled: true
 *       ignore-paths:
 *         - /actuator/**
 *       user-id-header: X-User-Id
 *       username-header: X-Username
 * </pre>
 *
 * @author zelda
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "zelda.security")
public class SecurityProperties {

    /**
     * OAuth2 授权服务器配置
     */
    private Server server = new Server();

    /**
     * OAuth2 资源服务器配置
     */
    private Resource resource = new Resource();

    /**
     * 网关安全配置
     */
    private Gateway gateway = new Gateway();

    public Server getServer() { return server; }
    public void setServer(Server server) { this.server = server; }
    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }
    public Gateway getGateway() { return gateway; }
    public void setGateway(Gateway gateway) { this.gateway = gateway; }

    /**
     * OAuth2 授权服务器配置
     */
    public static class Server {
        /**
         * 授权码超时时间（秒），默认 5 分钟
         */
        private int codeTimeout = 300;

        /**
         * 访问令牌超时时间（秒），默认 2 小时
         */
        private int accessTokenTimeout = 7200;

        /**
         * 刷新令牌超时时间（秒），默认 30 天
         */
        private int refreshTokenTimeout = 2592000;

        /**
         * 自定义登录页面模板路径（classpath 下的相对路径）
         * 例如：templates/oauth2/login.html
         */
        private String loginTemplatePath;

        /**
         * 自定义授权确认页面模板路径（classpath 下的相对路径）
         * 例如：templates/oauth2/confirm.html
         */
        private String confirmTemplatePath;

        public int getCodeTimeout() { return codeTimeout; }
        public void setCodeTimeout(int codeTimeout) { this.codeTimeout = codeTimeout; }
        public int getAccessTokenTimeout() { return accessTokenTimeout; }
        public void setAccessTokenTimeout(int accessTokenTimeout) { this.accessTokenTimeout = accessTokenTimeout; }
        public int getRefreshTokenTimeout() { return refreshTokenTimeout; }
        public void setRefreshTokenTimeout(int refreshTokenTimeout) { this.refreshTokenTimeout = refreshTokenTimeout; }
        public String getLoginTemplatePath() { return loginTemplatePath; }
        public void setLoginTemplatePath(String loginTemplatePath) { this.loginTemplatePath = loginTemplatePath; }
        public String getConfirmTemplatePath() { return confirmTemplatePath; }
        public void setConfirmTemplatePath(String confirmTemplatePath) { this.confirmTemplatePath = confirmTemplatePath; }
    }

    /**
     * OAuth2 资源服务器配置
     */
    public static class Resource {
        /**
         * 是否启用资源服务器安全功能，默认 true
         */
        private boolean enabled = true;

        /**
         * 忽略认证的路径列表，默认包含 /actuator/**
         */
        private List<String> ignorePaths = new ArrayList<>();

        public Resource() { ignorePaths.add("/actuator/**"); }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public List<String> getIgnorePaths() { return ignorePaths; }
        public void setIgnorePaths(List<String> ignorePaths) { this.ignorePaths = ignorePaths; }
    }

    /**
     * 网关安全配置
     */
    public static class Gateway {
        /**
         * 是否启用网关安全功能，默认 true
         */
        private boolean enabled = true;

        /**
         * 忽略认证的路径列表，默认包含 /actuator/**
         */
        private List<String> ignorePaths = new ArrayList<>();

        /**
         * 用户 ID 请求头名称，默认 X-User-Id
         */
        private String userIdHeader = "X-User-Id";

        /**
         * 用户名请求头名称，默认 X-Username
         */
        private String usernameHeader = "X-Username";

        public Gateway() { ignorePaths.add("/actuator/**"); }

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public List<String> getIgnorePaths() { return ignorePaths; }
        public void setIgnorePaths(List<String> ignorePaths) { this.ignorePaths = ignorePaths; }
        public String getUserIdHeader() { return userIdHeader; }
        public void setUserIdHeader(String userIdHeader) { this.userIdHeader = userIdHeader; }
        public String getUsernameHeader() { return usernameHeader; }
        public void setUsernameHeader(String usernameHeader) { this.usernameHeader = usernameHeader; }
    }
}