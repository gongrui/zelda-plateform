package com.blue.platform.uaa.system.service;

import com.blue.platform.uaa.system.entity.App;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 应用服务接口
 *
 * @author gongrui
 */
public interface AppService extends IService<App> {

    /**
     * 根据应用编码查询应用
     *
     * @param code 应用编码
     * @return 应用信息
     */
    App getByCode(String code);

    /**
     * 查询所有启用的应用
     *
     * @return 应用列表
     */
    List<App> listEnabledApps();

    /**
     * 验证应用是否可用
     *
     * @param appId 应用 ID
     * @return true-可用；false-不可用
     */
    boolean validateApp(String appId);
}
