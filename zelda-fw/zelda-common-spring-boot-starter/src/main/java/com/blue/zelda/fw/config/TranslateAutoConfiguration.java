package com.blue.zelda.fw.config;

import com.blue.zelda.fw.trans.AutoTranslateSerializer;
import com.blue.zelda.fw.trans.TranslateCacheService;
import com.blue.zelda.fw.trans.TranslateDataProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Boot 4 兼容修复版
 * 自动注入 Jackson2 的 ObjectMapper
 */
@AutoConfiguration
@EnableCaching
@ConditionalOnMissingClass("com.blue.zelda.fw.trans.TranslateCacheService")
public class TranslateAutoConfiguration {

    // 只保留 ObjectMapper，解决 Spring Boot4 核心报错
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Object.class, new AutoTranslateSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}