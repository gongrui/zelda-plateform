package com.blue.platform.uaa.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaCookie;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerate;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SaOAuth2ServerH5Controller {





    @PostMapping("/oauth2/getRedirectUri")
    public Object getRedirectUri() {
        SaRequest req = SaHolder.getRequest();
        SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();
        SaOAuth2DataGenerate dataGenerate = SaOAuth2Manager.getDataGenerate();
        SaOAuth2Template oauth2Template = SaOAuth2Manager.getTemplate();
        String responseType = req.getParamNotNull(SaOAuth2Consts.Param.response_type);

        // 1、判断授权模式
        SaOAuth2ServerProcessor.instance.checkAuthorizeResponseType(responseType, req, cfg);

        // 2、判断是否登录
        long loginId = SaOAuth2Manager.getStpLogic().getLoginId(0L);
        if (loginId == 0L) {
            // ====================== 关键：返回结构 100% 匹配前端 ======================
            Map<String, Object> map = new HashMap<>();
            map.put("code", 401);
            map.put("msg", "need login");
            map.put("data", new HashMap<>()); // 必须给空对象，前端才不报错
            return map;
        }

        // 3、构建请求Model
        RequestAuthModel ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, loginId);

        // 4、自定义授权检查
        SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(ra.loginId, ra.clientId);

        // 5、校验重定向域名
        oauth2Template.checkRedirectUri(ra.clientId, ra.redirectUri);

        // 6、校验Scope签约
        oauth2Template.checkContractScope(ra.clientId, ra.scopes);

        // 7、判断是否需要确认授权
        boolean isNeedCarefulConfirm = oauth2Template.isNeedCarefulConfirm(ra.loginId, ra.clientId, ra.scopes);
        if (isNeedCarefulConfirm) {
            SaClientModel cm = oauth2Template.checkClientModel(ra.clientId);
            if (!cm.getIsAutoConfirm()) {
                // ====================== 关键：返回结构 100% 匹配前端 ======================
                Map<String, Object> map = new HashMap<>();
                map.put("code", 411);
                map.put("msg", "need confirm");
                map.put("data", new HashMap<>()); // 必须给空对象
                return map;
            }
        }

        // 8、授权码模式：下放code
        if (SaOAuth2Consts.ResponseType.code.equals(ra.responseType)) {
            CodeModel codeModel = dataGenerate.generateCode(ra);
            String redirectUri = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);

            // ====================== 成功返回：data.redirect_uri ======================
            Map<String, Object> data = new HashMap<>();
            data.put("redirect_uri", redirectUri);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "ok");
            result.put("data", data);
            return result;
        }

        // 9、隐藏式：下放token
        if (SaOAuth2Consts.ResponseType.token.equals(ra.responseType)) {
            AccessTokenModel at = dataGenerate.generateAccessToken(ra, false, null);
            String redirectUri = dataGenerate.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);

            Map<String, Object> data = new HashMap<>();
            data.put("redirect_uri", redirectUri);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "ok");
            result.put("data", data);
            return result;
        }

        throw new SaOAuth2Exception("无效 response_type: " + ra.responseType).setCode(SaOAuth2ErrorCode.CODE_30125);
    }
}