package com.blue.platform.uaa.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
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
import cn.dev33.satoken.util.SaFoxUtil;
import com.blue.platform.uaa.auth.service.IamOAuth2Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 OAuth2 控制器，支持 PKCE
 *
 * @author gongrui
 */
@RestController
@RequestMapping("/oauth2")
public class CustomOAuth2Controller {

    private final IamOAuth2Template saOAuth2Template;

    /**
     * 授权端点 (支持 PKCE)
     */
    @GetMapping("/authorize")
    public Object authorize(
            @RequestParam(value = "response_type", required = false) String responseType,
            @RequestParam(value = "client_id", required = false) String clientId,
            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
            @RequestParam(value = "scope", required = false) String scope,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "code_challenge", required = false) String codeChallenge,
            @RequestParam(value = "code_challenge_method", required = false) String codeChallengeMethod
    ) {
        SaRequest req = SaHolder.getRequest();
        SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();
        SaOAuth2Template oauth2Template = SaOAuth2Manager.getTemplate();
        SaOAuth2DataGenerate dataGenerate = SaOAuth2Manager.getDataGenerate();

        // 处理未带参数的请求，显示登录页面
        if (responseType == null) {
            return "OAuth2 授权服务 - 请通过标准 OAuth2 流程访问";
        }

        // 1、判断授权模式
        SaOAuth2ServerProcessor.instance.checkAuthorizeResponseType(responseType, req, cfg);

        // 2、判断是否登录
        long loginId = SaOAuth2Manager.getStpLogic().getLoginId(0L);
        if (loginId == 0L) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", 401);
            map.put("msg", "need login");
            map.put("data", new HashMap<>());
            return map;
        }

        // 3、构建请求 Model
        RequestAuthModel ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, loginId);

        // 4、自定义授权检查
        SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(ra.loginId, ra.clientId);

        // 5、校验重定向域名
        oauth2Template.checkRedirectUri(ra.clientId, ra.redirectUri);

        // 6、校验 Scope 签约
        oauth2Template.checkContractScope(ra.clientId, ra.scopes);

        // 7、验证 PKCE 参数
        if (codeChallenge != null) {
            if (codeChallengeMethod == null) {
                return Map.of("error", "invalid_request", "error_description", "Missing code_challenge_method");
            }
            if (!Arrays.asList("S256", "plain").contains(codeChallengeMethod.toUpperCase())) {
                return Map.of("error", "invalid_request", "error_description", "Invalid code_challenge_method");
            }
        }

        // 8、判断是否需要确认授权
        boolean isNeedCarefulConfirm = oauth2Template.isNeedCarefulConfirm(ra.loginId, ra.clientId, ra.scopes);
        if (isNeedCarefulConfirm) {
            SaClientModel cm = oauth2Template.checkClientModel(ra.clientId);
            if (!cm.getIsAutoConfirm()) {
                Map<String, Object> map = new HashMap<>();
                map.put("code", 411);
                map.put("msg", "need confirm");
                map.put("data", new HashMap<>());
                return map;
            }
        }

        // 9、授权码模式：下放 code（包含 PKCE 参数）
        if (SaOAuth2Consts.ResponseType.code.equals(ra.responseType)) {
            CodeModel codeModel = dataGenerate.generateCode(ra);

            // 保存授权码时包含 PKCE 信息
            String scopeStr = ra.scopes != null ? String.join(" ", ra.scopes) : "";
            saOAuth2Template.saveAuthCode(
                    codeModel.code,
                    ra.clientId,
                    String.valueOf(ra.loginId),
                    scopeStr,
                    ra.redirectUri,
                    ra.state,
                    codeChallenge,
                    codeChallengeMethod
            );

            String redirectUriStr = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);

            Map<String, Object> data = new HashMap<>();
            data.put("redirect_uri", redirectUriStr);

            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "ok");
            result.put("data", data);
            return result;
        }


        throw new SaOAuth2Exception("无效 response_type: " + ra.responseType).setCode(SaOAuth2ErrorCode.CODE_30125);
    }

    /**
     * 令牌端点 (仅支持授权码模式和刷新令牌模式)
     */
    @PostMapping("/token")
    public Object token(
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_id") String clientId,
            @RequestParam(value = "client_secret", required = false) String clientSecret,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
            @RequestParam(value = "code_verifier", required = false) String codeVerifier,
            @RequestParam(value = "refresh_token", required = false) String refreshToken
    ) {
        SaOAuth2Template oauth2Template = SaOAuth2Manager.getTemplate();

        // 1、验证客户端
        SaClientModel clientModel = oauth2Template.checkClientModel(clientId);
        if (clientModel == null || clientModel.getClientSecret() == null) {
            return errorResponse("invalid_client", "Invalid client_id");
        }

        // 验证客户端密钥
        String clientSecretStr = clientModel.getClientSecret();
        if ((clientSecretStr == null || clientSecretStr.isEmpty() || "none".equalsIgnoreCase(clientSecretStr))
                && !clientSecretStr.equals(clientSecret)) {
            return errorResponse("invalid_client", "Invalid client_secret");
        }

        // 2、根据授权类型处理（仅支持授权码和刷新令牌）
        if ("authorization_code".equals(grantType)) {
            return handleAuthorizationCodeGrant(clientId, code, redirectUri, codeVerifier);
        } else if ("refresh_token".equals(grantType)) {
            return handleRefreshTokenGrant(clientId, refreshToken);
        }

        return errorResponse("unsupported_grant_type", "Only authorization_code and refresh_token grant types are supported");
    }

    /**
     * 处理授权码模式
     */
    private Object handleAuthorizationCodeGrant(String clientId, String code, String redirectUri, String codeVerifier) {
        // 1、验证授权码
        Map<String, Object> codeInfo = saOAuth2Template.getAuthCode(code);
        if (codeInfo == null) {
            return errorResponse("invalid_grant", "Invalid or expired authorization code");
        }

        // 2、验证客户端ID匹配
        if (!clientId.equals(codeInfo.get("clientId"))) {
            return errorResponse("invalid_grant", "Client ID does not match the one used in authorization request");
        }

        // 3、验证重定向URI匹配
        String storedRedirectUri = (String) codeInfo.get("redirectUri");
        if (redirectUri != null && !redirectUri.equals(storedRedirectUri)) {
            return errorResponse("invalid_grant", "Redirect URI does not match the one used in authorization request");
        }

        // 4、验证 PKCE
        String storedCodeChallenge = (String) codeInfo.get("codeChallenge");
        if (storedCodeChallenge != null) {
            if (codeVerifier == null) {
                return errorResponse("invalid_grant", "Missing code_verifier for PKCE validation");
            }
            if (!saOAuth2Template.validatePkce(code, codeVerifier)) {
                return errorResponse("invalid_grant", "PKCE verification failed");
            }
        }

        // 5、生成访问令牌和刷新令牌
        String accountId = (String) codeInfo.get("accountId");
        String tokenScope = (String) codeInfo.get("scope");

        StpUtil.login(accountId, true);

        SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();
        long accessTokenTimeout = cfg.getAccessTokenTimeout();
        long refreshTokenTimeout = cfg.getRefreshTokenTimeout();

        String accessToken = StpUtil.getTokenValue();
        String newRefreshToken = SaFoxUtil.getRandomString(32);

        // 保存令牌信息
        saOAuth2Template.saveAccessToken(accessToken, clientId, accountId, tokenScope, accessTokenTimeout);
        saOAuth2Template.saveRefreshToken(newRefreshToken, clientId, accountId, tokenScope, refreshTokenTimeout);

        // 删除已使用的授权码
        saOAuth2Template.deleteAuthCode(code);

        // 6、返回令牌
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("refresh_token", newRefreshToken);
        result.put("token_type", "Bearer");
        result.put("expires_in", accessTokenTimeout);
        if (tokenScope != null) {
            result.put("scope", tokenScope);
        }

        return result;
    }

    /**
     * 处理刷新令牌模式
     */
    private Object handleRefreshTokenGrant(String clientId, String refreshToken) {
        // 1、验证刷新令牌
        Map<String, Object> tokenInfo = saOAuth2Template.getRefreshTokenInfo(refreshToken);
        if (tokenInfo == null) {
            return errorResponse("invalid_grant", "Invalid or expired refresh token");
        }

        // 2、验证客户端ID匹配
        if (!clientId.equals(tokenInfo.get("clientId"))) {
            return errorResponse("invalid_grant", "Client ID does not match");
        }

        // 3、生成新的访问令牌
        String accountId = (String) tokenInfo.get("accountId");
        String tokenScope = (String) tokenInfo.get("scope");

        StpUtil.login(accountId, true);

        SaOAuth2ServerConfig cfg = SaOAuth2Manager.getServerConfig();
        long accessTokenTimeout = cfg.getAccessTokenTimeout();
        long refreshTokenTimeout = cfg.getRefreshTokenTimeout();

        String accessToken = StpUtil.getTokenValue();
        String newRefreshToken = SaFoxUtil.getRandomString(32);

        // 保存新的令牌信息
        saOAuth2Template.saveAccessToken(accessToken, clientId, accountId, tokenScope, accessTokenTimeout);
        saOAuth2Template.saveRefreshToken(newRefreshToken, clientId, accountId, tokenScope, refreshTokenTimeout);

        // 删除旧的刷新令牌
        saOAuth2Template.deleteRefreshToken(refreshToken);

        // 4、返回令牌
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", accessToken);
        result.put("refresh_token", newRefreshToken);
        result.put("token_type", "Bearer");
        result.put("expires_in", accessTokenTimeout);
        if (tokenScope != null) {
            result.put("scope", tokenScope);
        }

        return result;
    }


    /**
     * 构建错误响应
     */
    private Map<String, String> errorResponse(String error, String errorDescription) {
        Map<String, String> result = new HashMap<>();
        result.put("error", error);
        result.put("error_description", errorDescription);
        return result;
    }

    /**
     * 令牌校验接口
     */
    @GetMapping("/check_token")
    public Object checkToken(@RequestParam("token") String token) {
        Map<String, Object> tokenInfo = saOAuth2Template.getAccessTokenInfo(token);
        if (tokenInfo == null) {
            return errorResponse("invalid_token", "Token is invalid or expired");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("active", true);
        result.put("client_id", tokenInfo.get("clientId"));
        result.put("account_id", tokenInfo.get("accountId"));
        result.put("scope", tokenInfo.get("scope"));
        result.put("issued_at", tokenInfo.get("issueTime"));
        result.put("token_type", "Bearer");

        return result;
    }

    /**
     * 撤销令牌接口
     */
    @PostMapping("/revoke")
    public Object revokeToken(@RequestParam(value = "token", required = false) String accessToken,
                              @RequestParam(value = "token_type_hint", required = false) String tokenTypeHint) {
        if (accessToken != null) {
            saOAuth2Template.deleteAccessToken(accessToken);
        }
        return Map.of();
    }
}
