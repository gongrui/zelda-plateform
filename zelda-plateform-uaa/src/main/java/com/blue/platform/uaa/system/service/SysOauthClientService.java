package com.blue.platform.uaa.system.service;

import com.blue.platform.uaa.system.entity.SysOauthClient;

/**
 * OAuth2 客户端服务接口
 *
 * @author gongrui
 */
public interface SysOauthClientService {

    /**
     * 根据 clientId 查询客户端
     */
    SysOauthClient getByClientId(String clientId);

    /**
     * 根据 clientId 查询启用的客户端
     */
    SysOauthClient getActiveClientByClientId(String clientId);
}
