package com.blue.zelda.fw.trans;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;


@RequiredArgsConstructor
@ConditionalOnMissingBean(TranslateDataProvider.class)
public class TranslateCacheService {

    public static final String DICT = "dictCache";
    public static final String USER = "userCache";
    public static final String ORG = "orgCache";
    public static final String ROLE = "roleCache";

    private final TranslateDataProvider dataProvider;

    // ==================== 字典缓存 ====================
    @Cacheable(value = DICT, key = "'all'")
    public Map<String, Map<String, String>> dictMap() {
        return dataProvider.getAllDict();
    }

    // ==================== 用户缓存 ====================
    @Cacheable(value = USER, key = "'all'")
    public Map<String, String> userMap() {
        return dataProvider.getAllUser();
    }

    // ==================== 组织缓存 ====================
    @Cacheable(value = ORG, key = "'all'")
    public Map<String, String> orgMap() {
        return dataProvider.getAllOrg();
    }

    // ==================== 角色缓存 ====================
    @Cacheable(value = ROLE, key = "'all'")
    public Map<String, String> roleMap() {
        return dataProvider.getAllRole();
    }

    // ==================== 快捷获取 ====================
    public String getDict(String dictCode, String code) {
        return dictMap().getOrDefault(dictCode, Map.of()).getOrDefault(code, code);
    }

    public String getUserName(String id) {
        return userMap().getOrDefault(id, id);
    }

    public String getOrgName(String id) {
        return orgMap().getOrDefault(id, id);
    }

    public String getRoleName(String id) {
        return roleMap().getOrDefault(id, id);
    }
}