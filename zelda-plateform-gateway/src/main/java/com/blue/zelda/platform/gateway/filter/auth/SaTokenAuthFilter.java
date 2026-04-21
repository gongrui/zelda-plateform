package com.blue.zelda.platform.gateway.filter.auth;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Sa-Token 认证过滤器
 * 集成 Gateway 与 Sa-Token，实现统一的 OAuth2 认证
 */
@Slf4j
@Component
public class SaTokenAuthFilter implements GlobalFilter, Ordered {

    /**
     * 白名单路径（不需要认证）
     */
    private static final List<String> WHITE_LIST = List.of(
            "/oauth/token",
            "/oauth/authorize",
            "/login",
            "/logout",
            "/actuator/**",
            "/error"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 检查是否在白名单中
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 获取 Token
        String token = getTokenFromHeader(request);
        if (StrUtil.isBlank(token)) {
            return unauthorized(exchange, "未提供认证令牌");
        }

        // 验证 Token
        try {
            // 设置 Sa-Token 的 token 值
            StpUtil.setTokenValue(token);
            
            // 检查登录状态
            if (!StpUtil.isLogin()) {
                return unauthorized(exchange, "认证令牌无效或已过期");
            }

            // 将用户信息传递到下游服务
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", String.valueOf(StpUtil.getLoginId()))
                    .header("X-Username", StpUtil.getTokenSession().getString("username", ""))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            log.error("Sa-Token 认证失败：{}", e.getMessage());
            return unauthorized(exchange, "认证失败：" + e.getMessage());
        }
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream()
                .anyMatch(whitePath -> {
                    if (whitePath.endsWith("/**")) {
                        String prefix = whitePath.substring(0, whitePath.length() - 3);
                        return path.startsWith(prefix);
                    }
                    return path.equals(whitePath);
                });
    }

    /**
     * 从请求头获取 Token
     */
    private String getTokenFromHeader(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * 返回 401 未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        
        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        // 在路由过滤器之前执行
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
