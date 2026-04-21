package com.blue.zelda.platform.gateway.configuration;

import com.blue.zelda.platform.gateway.handle.ExceptionFallbackHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

/**
 * @author gongrui

 * 路由配置信息
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class RouterFunctionConfiguration {

    private final ExceptionFallbackHandler exceptionFallbackHandler;

    @Bean
    public RouterFunction<?> routerFunction() {
        return RouterFunctions.route(
                RequestPredicates.path("/fallback")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                exceptionFallbackHandler);

    }

}