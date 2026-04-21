package com.blue.zelda.platform.gateway.handle;

import com.alibaba.nacos.shaded.javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;

/**
 * 降级处理
 * 网关统一服务降级处理器
 *
 * 作用：服务宕机 / 超时 / 异常时，统一友好返回
 * 触发时机：后端微服务无法访问
 * 返回格式：标准 JSON，前端友好
 *
 * @author gongrui
 *
 */
@Slf4j
@Component
public class ExceptionFallbackHandler implements HandlerFunction<ServerResponse> {

    @Override
    @Nonnull
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        Optional<Object> originalUris = serverRequest.attribute(GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        originalUris.ifPresent(originalUri -> log.error("网关执行请求:{}失败,服务降级处理", originalUri));
        Map<String, Object> obj = new HashMap<>();
        obj.put("code", HttpStatus.SERVICE_UNAVAILABLE.value());
        obj.put("messageId", HttpStatus.SERVICE_UNAVAILABLE.value());
        obj.put("message", "服务繁忙，请稍后再试");
        obj.put("successful", false);
        obj.put("timestamp", System.currentTimeMillis());
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(obj));
    }
}
