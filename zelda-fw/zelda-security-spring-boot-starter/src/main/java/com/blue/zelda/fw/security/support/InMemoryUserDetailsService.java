package com.blue.zelda.fw.security.support;

import com.blue.zelda.fw.security.spi.OAuth2UserDetailsService;

import java.util.HashMap;
import java.util.Map;

/**
 * 内存用户详情服务实现
 *
 * <p>这是 {@link OAuth2UserDetailsService} 的默认实现，将用户信息存储在内存中。
 * 适用于开发环境和测试环境，生产环境建议使用数据库存储。</p>
 *
 * <p>当前实现为简化版，仅返回基础的用户信息，实际使用时应该根据 loginId 查询真实用户数据。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
public class InMemoryUserDetailsService implements OAuth2UserDetailsService {

    /**
     * 根据登录 ID 获取用户信息
     *
     * <p>当前实现返回模拟的用户信息，实际使用时应该从数据库或其他存储中查询。</p>
     *
     * @param loginId 登录 ID
     * @return 用户信息 Map
     */
    @Override
    public Map<String, Object> getUserByLoginId(Object loginId) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", loginId.toString());
        user.put("username", "user_" + loginId);
        return user;
    }
}