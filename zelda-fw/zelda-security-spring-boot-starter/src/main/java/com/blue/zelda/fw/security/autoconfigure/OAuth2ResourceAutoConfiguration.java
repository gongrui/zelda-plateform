package com.blue.zelda.fw.security.autoconfigure;

import cn.dev33.satoken.stp.StpUtil;
import com.blue.zelda.fw.security.annotation.IgnoreAuth;
import com.blue.zelda.fw.security.config.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Method;

/**
 * OAuth2 资源服务器自动配置类
 *
 * <p>为传统的 Servlet Web 应用提供 OAuth2 认证功能，包括：</p>
 * <ul>
 *   <li>基于拦截器的请求认证</li>
 *   <li>支持 {@link IgnoreAuth} 注解忽略特定接口的认证</li>
 *   <li>可配置的忽略路径列表</li>
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
public class OAuth2ResourceAutoConfiguration implements WebMvcConfigurer {

    private final SecurityProperties properties;

    /**
     * 添加认证拦截器
     *
     * <p>拦截所有请求，排除配置的忽略路径，验证用户登录状态。
     * 如果控制器方法或类上标注了 {@link IgnoreAuth} 注解，则跳过认证。</p>
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(properties))
                .addPathPatterns("/**")
                .excludePathPatterns(properties.getResource().getIgnorePaths());
    }

    /**
     * 认证拦截器
     *
     * <p>在请求处理前检查用户登录状态，支持通过 {@link IgnoreAuth} 注解跳过认证。</p>
     */
    @RequiredArgsConstructor
    public static class AuthInterceptor implements HandlerInterceptor {
        private final SecurityProperties properties;

        /**
         * 请求处理前的拦截逻辑
         *
         * @param request 当前请求
         * @param response 当前响应
         * @param handler 处理器对象
         * @return true 继续处理，false 中断处理
         */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            if (!properties.getResource().isEnabled()) return true;
            if (!(handler instanceof HandlerMethod hm)) return true;
            if (isIgnored(hm)) return true;
            StpUtil.checkLogin();
            return true;
        }

        /**
         * 判断处理器是否应该被忽略认证
         *
         * <p>如果处理器方法或其所属类上标注了 {@link IgnoreAuth} 注解，则返回 true。</p>
         *
         * @param hm 处理器方法
         * @return true 表示忽略认证，false 表示需要认证
         */
        private boolean isIgnored(HandlerMethod hm) {
            Method method = hm.getMethod();
            Class<?> beanType = hm.getBeanType();
            return method.isAnnotationPresent(IgnoreAuth.class)
                    || beanType.isAnnotationPresent(IgnoreAuth.class);
        }
    }

}