package com.blue.platform.uaa.util;

import cn.dev33.satoken.stp.StpUtil;
import com.blue.platform.uaa.system.entity.SysUser;
import com.blue.platform.uaa.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 *
 * @author gongrui
 */
@Component
public class SecurityUtil {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取当前登录用户
     */
    public SysUser getCurrentUser() {
        if (!StpUtil.isLogin()) {
            return null;
        }

        Object loginId = StpUtil.getLoginId();
        if (loginId instanceof String) {
            return sysUserService.getByUsername((String) loginId);
        } else if (loginId instanceof Long) {
            return sysUserService.getByUsername(String.valueOf(loginId));
        }

        return null;
    }

    /**
     * 获取当前登录用户ID
     */
    public String getCurrentUserId() {
        if (!StpUtil.isLogin()) {
            return null;
        }

        Object loginId = StpUtil.getLoginId();
        if (loginId instanceof String) {
            return (String) loginId;
        } else if (loginId instanceof Long) {
            return String.valueOf(loginId);
        }

        return null;
    }

    /**
     * 获取当前登录用户名
     */
    public String getCurrentUsername() {
        SysUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }
}
