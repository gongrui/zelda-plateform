package com.blue.platform.uaa.auth.service;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import com.blue.platform.uaa.system.entity.SysOauthClient;
import com.blue.platform.uaa.system.service.SysOauthClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IAM OAuth2 模板实现
 * 提供 OAuth2 核心功能实现，支持 PKCE
 *
 * @author gongrui
 */
@Slf4j
@Component
public class IamOAuth2Template extends SaOAuth2Template {

    private static final String CODE_PREFIX = "satoken:oauth2:pkce:code:";
    private static final String ACCESS_PREFIX = "satoken:oauth2:pkce:access:";
    private static final String REFRESH_PREFIX = "satoken:oauth2:pkce:refresh:";

    @Autowired
    private SysOauthClientService sysOauthClientService;

    /**
     * 判断是否已授权
     */
    @Override
    public boolean isGrantScope(Object loginId, String clientId, List<String> scopes) {
        // 直接返回 true = 自动授权，不弹窗
        return true;
    }

    /**
     * 保存授权记录
     */
    @Override
    public void saveGrantScope(String clientId, Object loginId, List<String> scopes) {
        // 保存授权记录到数据库（这里测试留空）
        log.debug("保存授权记录: clientId={}, loginId={}, scopes={}", clientId, loginId, scopes);
    }

    /**
     * 获取客户端信息（从数据库加载）
     */
    @Override
    public SaClientModel getClientModel(String clientId) {
        // 先尝试从数据库获取
        SysOauthClient client = sysOauthClientService.getByClientId(clientId);
        if (client != null) {
            // 解析重定向URI
            String redirectUris = client.getRedirectUris();
            java.util.List<String> allowRedirectUris = redirectUris != null && !redirectUris.isEmpty()
                    ? Arrays.asList(redirectUris.split(","))
                    : Arrays.asList("*");

            // 解析授权类型
            String grantTypes = client.getAuthorizedGrantTypes();
            java.util.List<String> allowGrantTypes = grantTypes != null && !grantTypes.isEmpty()
                    ? Arrays.asList(grantTypes.split(","))
                    : Arrays.asList("authorization_code", "refresh_token");

            // 解析作用域
            String scopes = client.getScopes();
            java.util.List<String> contractScopes = scopes != null && !scopes.isEmpty()
                    ? Arrays.asList(scopes.split(","))
                    : Arrays.asList("openid", "userinfo");

            return new SaClientModel()
                    .setClientId(client.getClientId())
                    .setClientSecret(client.getClientSecret())
                    .setAllowRedirectUris(allowRedirectUris)
                    .setContractScopes(contractScopes)
                    .setAllowGrantTypes(allowGrantTypes);
        }
        return null;
    }

    // ==================== PKCE 支持 ====================

    /**
     * 保存授权码 (包含 PKCE 参数)
     */
    public void saveAuthCode(String code, String clientId, String accountId, String scope,
                              String redirectUri, String state, String codeChallenge, String codeChallengeMethod) {
        Map<String, Object> codeInfo = new HashMap<>();
        codeInfo.put("clientId", clientId);
        codeInfo.put("accountId", accountId);
        codeInfo.put("scope", scope);
        codeInfo.put("redirectUri", redirectUri);
        codeInfo.put("state", state);
        codeInfo.put("codeChallenge", codeChallenge);
        codeInfo.put("codeChallengeMethod", codeChallengeMethod);

        long timeoutSeconds = SaOAuth2Manager.getServerConfig().getCodeTimeout();
        SaManager.getSaTokenDao().setObject(CODE_PREFIX + code, codeInfo, timeoutSeconds);
    }

    /**
     * 获取授权码信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAuthCode(String code) {
        return (Map<String, Object>) SaManager.getSaTokenDao().getObject(CODE_PREFIX + code);
    }

    /**
     * 删除授权码
     */
    public void deleteAuthCode(String code) {
        SaManager.getSaTokenDao().deleteObject(CODE_PREFIX + code);
    }

    /**
     * 验证 PKCE
     */
    public boolean validatePkce(String code, String codeVerifier) {
        Map<String, Object> codeInfo = getAuthCode(code);
        if (codeInfo == null) {
            return false;
        }

        String storedCodeChallenge = (String) codeInfo.get("codeChallenge");
        String storedCodeChallengeMethod = (String) codeInfo.get("codeChallengeMethod");

        if (storedCodeChallenge == null || storedCodeChallengeMethod == null) {
            return true;
        }

        String calculatedCodeChallenge;
        if ("S256".equalsIgnoreCase(storedCodeChallengeMethod)) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
                calculatedCodeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 algorithm not available", e);
            }
        } else if ("plain".equalsIgnoreCase(storedCodeChallengeMethod)) {
            calculatedCodeChallenge = codeVerifier;
        } else {
            return false;
        }

        return MessageDigest.isEqual(
                storedCodeChallenge.getBytes(StandardCharsets.UTF_8),
                calculatedCodeChallenge.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 保存访问令牌
     */
    public void saveAccessToken(String accessToken, String clientId, String accountId, String scope, long timeout) {
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("clientId", clientId);
        tokenInfo.put("accountId", accountId);
        tokenInfo.put("scope", scope);
        tokenInfo.put("issueTime", System.currentTimeMillis());
        SaManager.getSaTokenDao().setObject(ACCESS_PREFIX + accessToken, tokenInfo, timeout);
    }

    /**
     * 获取访问令牌信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAccessTokenInfo(String accessToken) {
        return (Map<String, Object>) SaManager.getSaTokenDao().getObject(ACCESS_PREFIX + accessToken);
    }

    /**
     * 删除访问令牌
     */
    public void deleteAccessToken(String accessToken) {
        SaManager.getSaTokenDao().deleteObject(ACCESS_PREFIX + accessToken);
    }

    /**
     * 保存刷新令牌
     */
    public void saveRefreshToken(String refreshToken, String clientId, String accountId, String scope, long timeout) {
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("clientId", clientId);
        tokenInfo.put("accountId", accountId);
        tokenInfo.put("scope", scope);
        tokenInfo.put("issueTime", System.currentTimeMillis());
        SaManager.getSaTokenDao().setObject(REFRESH_PREFIX + refreshToken, tokenInfo, timeout);
    }

    /**
     * 获取刷新令牌信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRefreshTokenInfo(String refreshToken) {
        return (Map<String, Object>) SaManager.getSaTokenDao().getObject(REFRESH_PREFIX + refreshToken);
    }

    /**
     * 删除刷新令牌
     */
    public void deleteRefreshToken(String refreshToken) {
        SaManager.getSaTokenDao().deleteObject(REFRESH_PREFIX + refreshToken);
    }
}
