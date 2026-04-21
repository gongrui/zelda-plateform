package com.blue.zelda.fw.security.support;

import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import com.blue.zelda.fw.security.config.SecurityProperties;
import com.blue.zelda.fw.security.spi.OAuth2ClientDetailsService;

import java.util.Arrays;

/**
 * 内存客户端详情服务实现
 *
 * <p>这是 {@link OAuth2ClientDetailsService} 的默认实现，将客户端信息存储在内存中。
 * 适用于开发环境和测试环境，生产环境建议使用数据库存储。</p>
 *
 * <p>默认配置：</p>
 * <ul>
 *   <li>客户端 ID: 1001</li>
 *   <li>客户端密钥: aaaa-bbbb-cccc-dddd</li>
 *   <li>授权范围: openid, userinfo</li>
 *   <li>支持授权模式: authorization_code, refresh_token</li>
 * </ul>
 *
 * @author zelda
 * @since 1.0.0
 */
public class InMemoryClientDetailsService implements OAuth2ClientDetailsService {
    private final SecurityProperties properties;

    /**
     * 构造函数
     *
     * @param properties 安全配置属性
     */
    public InMemoryClientDetailsService(SecurityProperties properties) {
        this.properties = properties;
    }

    /**
     * 根据客户端 ID 获取客户端信息
     *
     * <p>当前实现返回一个硬编码的客户端信息，实际使用时应该根据 clientId 参数查询。</p>
     *
     * @param clientId 客户端 ID
     * @return 客户端模型
     */
    @Override
    public SaClientModel getClientByClientId(String clientId) {
        return new SaClientModel()
                .setClientId("1001")
                .setClientSecret("aaaa-bbbb-cccc-dddd")
                .setAllowRedirectUris(Arrays.asList("*"))
                .setContractScopes(Arrays.asList("openid", "userinfo"))
                .setAllowGrantTypes(Arrays.asList(
                        "authorization_code",
                        "refresh_token"
                ));
    }
}