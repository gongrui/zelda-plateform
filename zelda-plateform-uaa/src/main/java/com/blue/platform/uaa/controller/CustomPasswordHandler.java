//package com.blue.platform.uaa.controller;
//
//import cn.dev33.satoken.context.SaHolder;
//import cn.dev33.satoken.context.model.SaCookie;
//import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
//import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
//import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
//import cn.dev33.satoken.oauth2.granttype.handler.PasswordGrantTypeHandler;
//import cn.dev33.satoken.oauth2.granttype.handler.model.PasswordAuthResult;
//import jakarta.servlet.http.Cookie;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomPasswordHandler implements PasswordGrantTypeHandler {
//
//
//
////    /**
////     * 正确写法：不加 @Override，直接重写方法
////     * 对照你提供的官方源码：方法名、参数、返回值 完全一致
////     */
////    public PasswordAuthResult loginByUsernamePassword(String username, String password) {
////        // 你的业务：校验用户名密码
////        if ("admin".equals(username) && "123456".equals(password)) {
////            // 返回登录ID，必须正确返回
////            return new PasswordAuthResult("10001");
////        }
////        // 校验失败抛出异常
////        throw new SaOAuth2Exception("账号或密码错误");
////    }
////
////    /**
////     * 扩展：获取 refresh_token（官方 1.45.0 标准方式）
////     * 我们换个更可靠的方式拿，不用 afterGenerateToken
////     */
////    @Override
////    public AccessTokenModel getAccessToken(cn.dev33.satoken.context.model.SaRequest req, String clientId, java.util.List<String> scopes) {
////        // 1、生成 Token（包含 access_token + refresh_token）
////       /* AccessTokenModel at = super.getAccessToken(req, clientId, scopes);
////
////        // 2、这里直接拿到 refresh_token（后端核心获取点）
////        String refreshToken = at.refreshToken;
////        String accessToken = at.accessToken;
////        Object loginId = at.loginId;
////
////        System.out.println("✅ 成功获取 refresh_token = " + refreshToken);
////        System.out.println("✅ 成功获取 access_token = " + accessToken);
////
////        SaHolder.getResponse().addCookie(new SaCookie("access_token", accessToken).setHttpOnly(true).setSameSite("Lax").setSecure(true).setMaxAge(7200));
////        SaHolder.getResponse().addCookie(new SaCookie("refresh_token", refreshToken).setHttpOnly(true).setSameSite("Lax").setSecure(true).setMaxAge(2592000));
////
////
////        // 你可以在这里存库、存Redis、绑定用户
//////*/
//////        return at;
////    }
//
//}
