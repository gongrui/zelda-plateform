package com.blue.zelda.fw.trans;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j

@RequiredArgsConstructor
public class TranslateCacheInitializer {

    private final TranslateCacheService translateCacheService;

    @PostConstruct
    public void init() {
        log.info("===== 开始预加载翻译缓存 =====");
        translateCacheService.dictMap();
        translateCacheService.userMap();
        translateCacheService.orgMap();
        translateCacheService.roleMap();
        log.info("===== 翻译缓存预加载完成 =====");
    }
}
