package com.blue.platform.uaa.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.util.SaFoxUtil;
import com.blue.platform.uaa.system.entity.SysOauthClient;
import com.blue.platform.uaa.system.service.SysOauthClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 OAuth2 模板，增加对 PKCE 的支持
 */
@Component
@RequiredArgsConstructor
public class CustomSaOAuth2Template extends SaOAuth2Template {

    private final SaTokenDao saTokenDao;
    
    private final SysOauthClientService sysOauthClientService;

    // 自定义方法来存储包含 PKCE 信息的授权码
    public void saveAuthCodeWithPkce(String code, String clientId, String accountId, String scope, String redirectUri, String state, String codeChallenge, String codeChallengeMethod) {
        CodeModel codeModel = new CodeModel();
        codeModel.code = code;

        codeModel.clientId = clientId;
        codeModel.accountId = accountId;
        codeModel.scope = scope != null ? SaFoxUtil.toList(scope, ",") : null;
        codeModel.redirectUri = redirectUri;
        codeModel.state = state;

        // 存储 PKCE 相关信息到扩展属性中
        Map<String, Object> extParams = new HashMap<>();
        extParams.put("codeChallenge", codeChallenge);
        extParams.put("codeChallengeMethod", codeChallengeMethod);
        codeModel.extParams = extParams;

        // 设置过期时间
        long timeoutSeconds = SaOAuth2Config.getAuthCodeTimeout();
        saTokenDao.setObject("satoken:oauth2:code:" + code, codeModel, timeoutSeconds);
    }

    // 获取包含 PKCE 信息的授权码模型
    public CodeModel getAuthCodeWithPkce(String code) {
        return (CodeModel) saTokenDao.getObject("satoken:oauth2:code:" + code);
    }

    /**
     * 验证 PKCE
     */
    public boolean validatePkce(String code, String codeVerifier) {
        CodeModel codeModel = getAuthCodeWithPkce(code);
        if (codeModel == null || codeModel.extParams == null) {
            return false; // 授权码不存在或已过期，或者没有 PKCE 信息
        }

        String storedCodeChallenge = (String) codeModel.extParams.get("codeChallenge");
        String storedCodeChallengeMethod = (String) codeModel.extParams.get("codeChallengeMethod");

        if (storedCodeChallenge == null || storedCodeChallengeMethod == null) {
            // 如果授权码没有关联 PKCE 信息，则认为是普通流程，不强制验证 PKCE
            return false;
        }

        String calculatedCodeChallenge;
        if ("S256".equalsIgnoreCase(storedCodeChallengeMethod)) {
            // S256: SHA256(code_verifier) -> Base64URL-encoded
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
                calculatedCodeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 algorithm not available", e);
            }
        } else if ("plain".equalsIgnoreCase(storedCodeChallengeMethod)) {
            // plain: code_verifier == code_challenge
            calculatedCodeChallenge = codeVerifier;
        } else {
            // 不支持的 method
            return false;
        }

        return MessageDigest.isEqual(storedCodeChallenge.getBytes(), calculatedCodeChallenge.getBytes());
    }

    /**
     * 获取客户端信息
     */
    @Override
    public cn.dev33.satoken.oauth2.data.model.loader.SaClientModel getClientModel(String clientId) {
        SysOauthClient client = sysOauthClientService.lambdaQuery()
                .eq(SysOauthClient::getClientId, clientId)
                .eq(SysOauthClient::getStatus, 1)
                .one();

        if (client == null) {
            return null;
        }

        cn.dev33.satoken.oauth2.data.model.loader.SaClientModel clientModel = new cn.dev33.satoken.oauth2.data.model.loader.SaClientModel();
        clientModel.clientId = client.getClientId();
        clientModel.clientSecret = client.getClientSecret();
        clientModel.clientName = client.getClientName();
        clientModel.description = client.getDescription();
        clientModel.allowRedirectUris = SaFoxUtil.convertJsonToList(client.getRedirectUris(), String.class);
        clientModel.allowClientTypes = SaFoxUtil.convertJsonToList(client.getAuthorizedGrantTypes(), String.class);
        clientModel.contractScopes = SaFoxUtil.convertJsonToList(client.getScopes(), String.class);
        clientModel.accessTokenTimeout = client.getAccessTokenValidity() != null ? client.getAccessTokenValidity() : 7200;
        clientModel.refreshTokenTimeout = client.getRefreshTokenValidity() != null ? client.getRefreshTokenValidity() : 2592000;
        clientModel.clientType = client.getClientType(); // "PUBLIC" or "CONFIDENTIAL"

        return clientModel;
    }
}