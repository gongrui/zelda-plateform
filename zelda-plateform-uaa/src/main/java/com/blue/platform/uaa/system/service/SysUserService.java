package com.blue.platform.uaa.system.service;

import com.blue.platform.uaa.system.entity.SysUser;

/**
 * 系统用户服务接口
 *
 * @author gongrui
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);
}
