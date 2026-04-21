package com.blue.platform.uaa.system.service.impl;

import com.blue.platform.uaa.system.entity.App;
import com.blue.platform.uaa.system.mapper.AppMapper;
import com.blue.platform.uaa.system.service.AppService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用服务实现类
 *
 * @author gongrui
 */
@Service
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {

    private final AppMapper appMapper;

    @Override
    public App getByCode(String code) {
        return appMapper.selectOneByQuery(
            QueryWrapper.create().eq(App::getCode, code)
        );
    }

    @Override
    public List<App> listEnabledApps() {
        return appMapper.selectListByQuery(
            QueryWrapper.create().eq(App::getStatus, true)
        );
    }

    @Override
    public boolean validateApp(String appId) {
        App app = appMapper.selectById(appId);
        return app != null && Boolean.TRUE.equals(app.getStatus());
    }
}
