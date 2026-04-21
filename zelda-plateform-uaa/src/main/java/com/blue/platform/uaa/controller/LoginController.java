package com.blue.platform.uaa.controller;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.blue.zelda.fw.core.entity.Result;
import com.blue.zelda.fw.security.annotation.IgnoreAuth;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    @IgnoreAuth
    /**
     * 登录提交（官方标准写法）
     */
    @PostMapping("/doLogin")
    public SaResult doLogin(
            @RequestParam String username,
            @RequestParam String password
    ) {
        // 1. 校验账号密码
        if (!"admin".equals(username) || !"123456".equals(password)) {
            return SaResult.error("账号或密码错误");
        }

        // 2. 登录
        SaOAuth2Manager.getStpLogic().login(1001);

        // 3. 回到 OAuth2 授权流程（官方标准）
        return SaResult.ok();

    }




}
