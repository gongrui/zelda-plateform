package com.blue.platform.uaa.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.blue.zelda.fw.security.spi.OAuth2LoginHandler;
import org.springframework.stereotype.Service;

/**
 * IAM项目：实际登录业务实现
 * 所有账号校验/查库/密码加密/租户逻辑全写这里
 */
@Service
public class OAuth2LoginHandlerImpl implements OAuth2LoginHandler {

    @Override
    public SaResult doLogin(String name, String pwd) {
        // ==================== 这里写你的IAM真实登录业务 ====================
        // 示例：数据库查询、密码BCrypt比对、禁用判断、验证码...
        if ("admin".equals(name) && "123456".equals(pwd)) {
            // 登录打入Sa-Token会话
            StpUtil.login(10001L);
            return SaResult.ok();
        }
        return SaResult.error("账号或密码错误");
    }
}
