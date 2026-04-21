package com.blue.zelda.fw.security.autoconfigure;

import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.util.SaFoxUtil;
import com.blue.zelda.fw.security.config.SecurityProperties;
import com.blue.zelda.fw.security.spi.OAuth2ClientDetailsService;
import com.blue.zelda.fw.security.spi.OAuth2LoginHandler;
import com.blue.zelda.fw.security.spi.OAuth2UserDetailsService;
import com.blue.zelda.fw.security.support.InMemoryClientDetailsService;
import com.blue.zelda.fw.security.support.InMemoryUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OAuth2 授权服务器自动配置类
 *
 * <p>为 Servlet Web 应用提供 OAuth2 授权服务器功能，包括：</p>
 * <ul>
 *   <li>支持多种授权模式：授权码、密码模式、客户端凭证、简化模式</li>
 *   <li>Token 颁发、刷新、校验等核心功能</li>
 *   <li>可配置的 Token 超时时间</li>
 *   <li>支持自定义登录和授权确认页面模板</li>
 *   <li>SPI 扩展点支持业务自定义实现</li>
 * </ul>
 *
 * <p>该配置仅在 Servlet Web 应用环境中生效。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RequiredArgsConstructor
public class OAuth2ServerAutoConfiguration {

    private final SecurityProperties properties;

    /**
     * 注入 IAM 项目实现的登录处理器
     */
    private final OAuth2LoginHandler oAuth2LoginHandler;

    /**
     * 配置 OAuth2 模板提供者
     *
     * @return OAuth2TemplateProvider 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2TemplateProvider templateProvider() {
        OAuth2TemplateProvider provider = new OAuth2TemplateProvider(properties);

        // 配置 OAuth2 策略
        configStrategy(provider);

        return provider;
    }

    /**
     * 配置 OAuth2 策略
     *
     * <p>配置未登录视图、登录处理逻辑和授权确认视图。</p>
     *
     * @param templateProvider 模板提供者
     */
    private void configStrategy(OAuth2TemplateProvider templateProvider) {
        // 配置：未登录时返回的 View
        // 优先使用自定义模板，否则使用内置模板
        SaOAuth2Strategy.instance.notLoginView = templateProvider::loadLoginTemplate;

        // 登录逻辑 → 委托给 IAM 的实现类
        // 将实际的登录验证逻辑委托给业务方实现
        SaOAuth2Strategy.instance.doLoginHandle = oAuth2LoginHandler::doLogin;

        // 配置：确认授权时返回的 view
        // 优先使用自定义模板，否则使用内置模板
        SaOAuth2Strategy.instance.confirmView = (clientId, scopes) -> {
            String template = templateProvider.loadConfirmTemplate();
            String scopeStr = SaFoxUtil.convertListToString(scopes);
            return templateProvider.fillConfirmTemplate(template, clientId, scopeStr);
        };
    }

    /**
     * 配置 OAuth2 客户端详情服务
     *
     * <p>默认使用内存实现，如果容器中存在其他实现，则优先使用。</p>
     *
     * @return OAuth2ClientDetailsService 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2ClientDetailsService oAuth2ClientDetailsService() {
        return new InMemoryClientDetailsService(properties);
    }

    /**
     * 配置 OAuth2 用户详情服务
     *
     * <p>默认使用内存实现，如果容器中存在其他实现，则优先使用。</p>
     *
     * @return OAuth2UserDetailsService 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2UserDetailsService oAuth2UserDetailsService() {
        return new InMemoryUserDetailsService();
    }

    /**
     * 配置 OAuth2 服务器核心配置
     *
     * <p>启用所有授权模式，设置各类 Token 的超时时间。</p>
     *
     * @return SaOAuth2ServerConfig 实例
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public SaOAuth2ServerConfig saOAuth2ServerConfig() {
        return new SaOAuth2ServerConfig()
                .setEnableAuthorizationCode(true)
                .setEnableImplicit(true)
                .setEnablePassword(true)
                .setEnableClientCredentials(true)
                .setCodeTimeout(properties.getServer().getCodeTimeout())
                .setAccessTokenTimeout(properties.getServer().getAccessTokenTimeout())
                .setRefreshTokenTimeout(properties.getServer().getRefreshTokenTimeout());
    }

    /**
     * 配置 OAuth2 数据加载器
     *
     * <p>将自定义的客户端详情服务适配到 Sa-Token 的数据加载器。</p>
     *
     * @param clientService 客户端详情服务
     * @param userService 用户详情服务
     * @return SaOAuth2DataLoader 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SaOAuth2DataLoader saOAuth2DataLoader(
            OAuth2ClientDetailsService clientService,
            OAuth2UserDetailsService userService) {
        return new SaOAuth2DataLoader() {
            @Override
            public SaClientModel getClientModel(String clientId) {
                return clientService.getClientByClientId(clientId);
            }
        };
    }

    /**
     * OAuth2 服务器控制器
     *
     * <p>处理所有 OAuth2 相关的请求，包括授权、Token 颁发等。</p>
     */
    @RestController
    public static class OAuth2ServerController {
        /**
         * 处理 OAuth2 请求
         *
         * @return 处理结果
         */
        @RequestMapping("/oauth2/*")
        public Object oauth2Request() {
            return SaOAuth2ServerProcessor.instance.dister();
        }
    }
}
