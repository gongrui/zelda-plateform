package com.blue.zelda.fw.autoconfigure;


import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * 元数据 Redis 缓存自动配置类
 *
 * <p>提供基于 Redis 的缓存管理器，用于缓存元数据信息（视图配置、数据源配置等）。
 * 当项目中存在 Redis 依赖且配置了 Redis 连接信息时自动生效。</p>
 *
 * <p>配置特性：</p>
 * <ul>
 *   <li>缓存过期时间：7 天</li>
 *   <li>Key 序列化：String 序列化</li>
 *   <li>Value 序列化：JSON 序列化</li>
 *   <li>不缓存 null 值</li>
 * </ul>
 *
 * <p>使用条件：</p>
 * <ul>
 *   <li>类路径中存在 {@link RedisConnectionFactory}</li>
 *   <li>配置文件中设置了 spring.redis.host</li>
 * </ul>
 *
 * @author zelda
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty("spring.redis.host")
public class MetaRedisCacheConfiguration {

    /**
     * 配置 Redis 缓存管理器
     *
     * <p>使用 Redis 作为缓存存储，提供分布式缓存支持。
     * 使用 @Primary 注解，确保在有多个 CacheManager 时优先使用此实现。</p>
     *
     * @param factory Redis 连接工厂
     * @return RedisCacheManager 实例
     */
    @Bean
    @Primary // 重点：有多个同类型Bean时，我是首选
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        // 正确用法：必须用 SerializationContext 包装
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(7))
                // Key 序列化（正确！）
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                RedisSerializer.string()
                        )
                )
                // Value 序列化（正确！无废弃、无警告）
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                RedisSerializer.json()
                        )
                )
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}
