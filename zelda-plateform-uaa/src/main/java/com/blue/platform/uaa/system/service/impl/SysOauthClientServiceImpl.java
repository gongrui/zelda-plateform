package com.blue.platform.uaa.system.service.impl;

import com.blue.platform.uaa.system.entity.SysOauthClient;
import com.blue.platform.uaa.system.mapper.SysOauthClientMapper;
import com.blue.platform.uaa.system.service.SysOauthClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * OAuth2 客户端服务实现
 *
 * @author gongrui
 */
@Service
@RequiredArgsConstructor
public class SysOauthClientServiceImpl implements SysOauthClientService {

    private final SysOauthClientMapper sysOauthClientMapper;

    @Override
    public SysOauthClient getByClientId(String clientId) {
        return sysOauthClientMapper.selectOneByQuery(
                com.mybatisflex.core.query.QueryWrapper.create()
                        .eq("client_id", clientId)
        );
    }

    @Override
    public SysOauthClient getActiveClientByClientId(String clientId) {
        return sysOauthClientMapper.selectOneByQuery(
                com.mybatisflex.core.query.QueryWrapper.create()
                        .eq("client_id", clientId)
                        .eq("status", true)
                        .eq("deleted", false)
        );
    }
}
