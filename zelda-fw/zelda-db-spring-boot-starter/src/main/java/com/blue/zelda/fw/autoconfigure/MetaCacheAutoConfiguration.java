package com.blue.zelda.fw.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

/**
 * 元数据缓存自动配置类
 *
 * <p>提供基于内存的默认缓存管理器，用于缓存元数据信息（视图配置、数据源配置等）。
 * 如果 Spring 容器中不存在其他 CacheManager Bean，则使用此默认实现。</p>
 *
 * <p>默认使用 {@link ConcurrentMapCacheManager}，适用于单机应用和开发环境。
 * 生产环境建议使用 {@link MetaRedisCacheConfiguration} 配置 Redis 缓存。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@AutoConfiguration
@EnableCaching
public class MetaCacheAutoConfiguration {

    /**
     * 配置默认的缓存管理器
     *
     * <p>基于内存的并发缓存管理器，使用 Map 存储缓存数据。
     * 当容器中不存在其他 CacheManager Bean 时生效。</p>
     *
     * @return ConcurrentMapCacheManager 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }
}

