package com.blue.zelda.fw.security.spi;


import cn.dev33.satoken.util.SaResult;

/**
 * SPI扩展：OAuth2登录处理器（由IAM项目实现）
 */
public interface OAuth2LoginHandler {

    /**
     * 账号密码登录校验
     * @param name 账号
     * @param pwd 密码
     * @return SaResult
     */
    SaResult doLogin(String name, String pwd);
}