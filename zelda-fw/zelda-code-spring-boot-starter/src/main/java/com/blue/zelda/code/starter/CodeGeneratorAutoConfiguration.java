package com.blue.zelda.code.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(StringRedisTemplate.class)
@EnableConfigurationProperties(CodeGeneratorProperties.class)
@ConditionalOnProperty(prefix = "zelda.code", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CodeGeneratorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SmartCodeGenerator smartCodeGenerator(
            StringRedisTemplate redisTemplate,
            CodeGeneratorProperties properties) {
        return new SmartCodeGenerator(redisTemplate, properties);
    }
}
