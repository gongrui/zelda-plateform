package com.blue.platform.uaa.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PKCE 工具类
 * 提供 code_verifier 生成和 code_challenge 计算功能
 *
 * @author gongrui
 */
public class PkceUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成随机的 code_verifier
     * 长度为 43-128 个字符，由 URL-safe 字符组成
     *
     * @return 随机生成的 code_verifier
     */
    public static String generateCodeVerifier() {
        byte[] buffer = new byte[32];
        SECURE_RANDOM.nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }

    /**
     * 使用 S256 方法计算 code_challenge
     * code_challenge = BASE64URL(SHA256(code_verifier))
     *
     * @param codeVerifier PKCE code_verifier
     * @return BASE64URL 编码的 SHA256 哈希值
     */
    public static String generateCodeChallengeS256(String codeVerifier) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * 验证 code_verifier 与 code_challenge 是否匹配
     *
     * @param codeVerifier PKCE code_verifier
     * @param codeChallenge PKCE code_challenge
     * @param method code_challenge_method (S256 或 plain)
     * @return 是否匹配
     */
    public static boolean verifyPkce(String codeVerifier, String codeChallenge, String method) {
        if (codeVerifier == null || codeChallenge == null || method == null) {
            return false;
        }

        String calculatedChallenge;
        if ("S256".equalsIgnoreCase(method)) {
            calculatedChallenge = generateCodeChallengeS256(codeVerifier);
        } else if ("plain".equalsIgnoreCase(method)) {
            calculatedChallenge = codeVerifier;
        } else {
            return false;
        }

        return MessageDigest.isEqual(
                codeChallenge.getBytes(StandardCharsets.UTF_8),
                calculatedChallenge.getBytes(StandardCharsets.UTF_8)
        );
    }
}
