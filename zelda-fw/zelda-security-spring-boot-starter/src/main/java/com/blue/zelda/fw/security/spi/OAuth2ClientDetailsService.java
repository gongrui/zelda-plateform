package com.blue.zelda.fw.security.spi;

import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;

/**
 * OAuth2 客户端详情服务接口
 *
 * <p>用于根据客户端 ID 获取客户端的配置信息，包括客户端密钥、允许的重定向 URI、
 * 授权范围、支持的授权类型等。</p>
 *
 * <p>该接口为 SPI 扩展点，业务方可以根据实际需求实现此接口，例如使用数据库存储客户端信息。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
public interface OAuth2ClientDetailsService {
    /**
     * 根据客户端 ID 获取客户端信息
     *
     * @param clientId 客户端 ID
     * @return 客户端模型，如果不存在则返回 null
     */
    SaClientModel getClientByClientId(String clientId);
}