package com.blue.zelda.fw.security.autoconfigure;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.blue.zelda.fw.security.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 网关自动配置类
 *
 * <p>为 Spring Cloud Gateway 提供 OAuth2 认证功能，包括：</p>
 * <ul>
 *   <li>基于 Sa-Token 的请求拦截和认证</li>
 *   <li>用户信息透传到下游服务</li>
 *   <li>未登录异常的全局处理</li>
 * </ul>
 *
 * <p>该配置仅在响应式 Web 应用（Reactive）环境中生效。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@RequiredArgsConstructor
public class OAuth2GatewayAutoConfiguration {

    private final SecurityProperties properties;

    /**
     * 配置 Sa-Token 响应式过滤器
     *
     * <p>拦截所有请求，排除配置的忽略路径，验证用户登录状态。</p>
     *
     * @return SaReactorFilter 实例
     */
    @Bean
    public SaReactorFilter saReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude(properties.getGateway().getIgnorePaths().toArray(new String[0]))
                .setAuth(obj -> {
                    if (!properties.getGateway().isEnabled()) return;
                    SaRouter.match("/**", StpUtil::checkLogin);
                });
    }

    /**
     * 配置用户信息透传过滤器
     *
     * <p>将当前登录用户的 ID 和用户名添加到请求头中，透传给下游服务。</p>
     *
     * @return UserInfoTransmitFilter 实例
     */
    @Bean
    public UserInfoTransmitFilter userInfoTransmitFilter() {
        return new UserInfoTransmitFilter(properties);
    }

    /**
     * 配置网关异常处理器
     *
     * @return GatewayExceptionHandler 实例
     */
    @Bean
    public GatewayExceptionHandler gatewayExceptionHandler() {
        return new GatewayExceptionHandler();
    }

    /**
     * 用户信息透传过滤器
     *
     * <p>从当前会话中获取用户信息，添加到请求头中透传给下游服务。
     * 执行顺序为 -100，确保在其他过滤器之前执行。</p>
     */
    @RequiredArgsConstructor
    public static class UserInfoTransmitFilter implements GlobalFilter, Ordered {
        private final SecurityProperties properties;

        /**
         * 过滤器处理逻辑
         *
         * @param exchange 当前请求交换对象
         * @param chain 过滤器链
         * @return 异步处理结果
         */
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            if (!properties.getGateway().isEnabled()) return chain.filter(exchange);
            try {
                Long userId = StpUtil.getLoginIdAsLong();
                String username = (String) StpUtil.getSession().get("username");
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header(properties.getGateway().getUserIdHeader(), String.valueOf(userId))
                        .header(properties.getGateway().getUsernameHeader(), username != null ? username : "")
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                return chain.filter(exchange);
            }
        }

        /**
         * 获取过滤器执行顺序
         *
         * @return 执行顺序，-100 表示高优先级
         */
        @Override
        public int getOrder() { return -100; }
    }

    /**
     * 网关全局异常处理器
     *
     * <p>处理网关层的异常，统一返回 JSON 格式的错误响应。</p>
     */
    @RestControllerAdvice
    @Order(-1)
    public static class GatewayExceptionHandler {
        /**
         * 处理未登录异常
         *
         * @param e 未登录异常
         * @param exchange 当前请求交换对象
         * @return 异步响应
         */
        @ExceptionHandler(NotLoginException.class)
        public Mono<Void> handleNotLogin(NotLoginException e, ServerWebExchange exchange) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = new HashMap<>();
            body.put("code", 401);
            body.put("msg", "Token 无效或已过期");
            return response.writeWith(Mono.just(
                    response.bufferFactory().wrap(body.toString().getBytes())
            ));
        }
    }
}