package com.blue.zelda.fw.security.spi;

import java.util.Map;

/**
 * OAuth2 用户详情服务接口
 *
 * <p>用于根据登录 ID 获取用户的详细信息，例如用户名、邮箱、手机号等扩展信息。
 * 该接口为 SPI 扩展点，业务方可以根据实际需求实现此接口。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
public interface OAuth2UserDetailsService {
    /**
     * 根据登录 ID 获取用户信息
     *
     * @param loginId 登录 ID，通常为用户 ID 或用户名
     * @return 用户信息 Map，如果不存在则返回 null
     */
    Map<String, Object> getUserByLoginId(Object loginId);
}