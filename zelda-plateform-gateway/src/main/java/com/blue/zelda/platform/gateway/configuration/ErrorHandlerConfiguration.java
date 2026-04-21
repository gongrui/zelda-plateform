package com.blue.zelda.platform.gateway.configuration;

import com.blue.zelda.platform.gateway.handle.JsonExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

import java.util.Collections;
import java.util.List;

/**
 * 覆盖默认的异常处理
 *
 * @author gongrui
 */
@Configuration
public class ErrorHandlerConfiguration {

    // 🔥 构造方法空的！什么都不注入！彻底解决所有 Bean 找不到问题！
    public ErrorHandlerConfiguration() {
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(
            ApplicationContext applicationContext,
            ServerCodecConfigurer serverCodecConfigurer,
            List<ViewResolver> viewResolvers
    ) {
        // 全部手动 new！不需要 Spring 注入！
        ErrorAttributes errorAttributes = new DefaultErrorAttributes();
        WebProperties.Resources resources = new WebProperties.Resources();
        ErrorProperties errorProperties = new ErrorProperties();

        JsonExceptionHandler handler = new JsonExceptionHandler(
                errorAttributes,
                resources,
                errorProperties,
                applicationContext
        );

        handler.setViewResolvers(viewResolvers);
        handler.setMessageWriters(serverCodecConfigurer.getWriters());
        handler.setMessageReaders(serverCodecConfigurer.getReaders());
        return handler;
    }
}