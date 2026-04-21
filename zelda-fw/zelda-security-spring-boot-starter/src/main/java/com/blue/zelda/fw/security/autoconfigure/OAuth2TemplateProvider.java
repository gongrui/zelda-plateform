package com.blue.zelda.fw.security.autoconfigure;

import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.util.SaFoxUtil;
import com.blue.zelda.fw.security.config.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 模板提供者
 *
 * <p>支持从外部文件或内部模板加载登录页面和授权确认页面。
 * 优先级：外部配置文件 > classpath 模板文件 > 默认内置模板</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class OAuth2TemplateProvider {

    private final SecurityProperties properties;

    /**
     * 默认登录页面模板
     */
    private static final String DEFAULT_LOGIN_TEMPLATE = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>OAuth2 登录</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 400px; margin: 100px auto; padding: 20px; }
                    input { width: 100%; padding: 10px; margin: 10px 0; box-sizing: border-box; }
                    button { width: 100%; padding: 10px; background: #007bff; color: white; border: none; cursor: pointer; }
                    button:hover { background: #0056b3; }
                </style>
            </head>
            <body>
                <h2>当前客户端在 OAuth-Server 认证中心尚未登录，请先登录</h2>
                <form id="loginForm">
                    <label>用户名：<input id="name" type="text" /></label>
                    <label>密码：<input id="pwd" type="password" /></label>
                    <button type="button" onclick="doLogin()">登录</button>
                </form>
                <div id="error" style="color: red; margin-top: 10px;"></div>
                <script>
                    function doLogin() {
                        const name = document.getElementById('name').value;
                        const pwd = document.getElementById('pwd').value;
                        fetch(`/oauth2/doLogin?name=${encodeURIComponent(name)}&pwd=${encodeURIComponent(pwd)}`)
                            .then(res => res.json())
                            .then(res => {
                                if(res.code === 200) {
                                    location.reload();
                                } else {
                                    document.getElementById('error').textContent = res.msg || '登录失败';
                                }
                            })
                            .catch(err => {
                                document.getElementById('error').textContent = '请求失败：' + err.message;
                            });
                    }
                </script>
            </body>
            </html>
            """;

    /**
     * 默认授权确认页面模板
     */
    private static final String DEFAULT_CONFIRM_TEMPLATE = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>OAuth2 授权确认</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 600px; margin: 100px auto; padding: 20px; }
                    .btn-group { margin-top: 20px; }
                    button { padding: 10px 30px; margin-right: 10px; border: none; cursor: pointer; }
                    .agree { background: #28a745; color: white; }
                    .reject { background: #dc3545; color: white; }
                    .agree:hover { background: #218838; }
                    .reject:hover { background: #c82333; }
                </style>
            </head>
            <body>
                <h2>授权确认</h2>
                <p>应用 <strong>{{CLIENT_ID}}</strong> 请求授权：<strong>{{SCOPES}}</strong>，是否同意？</p>
                <div class="btn-group">
                    <button class="agree" onclick="confirmAuth()">同意</button>
                    <button class="reject" onclick="rejectAuth()">拒绝</button>
                </div>
                <div id="error" style="color: red; margin-top: 10px;"></div>
                <script>
                    const clientId = '{{CLIENT_ID}}';
                    const scope = '{{SCOPES}}';

                    function confirmAuth() {
                        fetch('/oauth2/doConfirm', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                            body: `client_id=${encodeURIComponent(clientId)}&scope=${encodeURIComponent(scope)}`
                        })
                        .then(res => res.json())
                        .then(res => location.reload())
                        .catch(err => {
                            document.getElementById('error').textContent = '请求失败：' + err.message;
                        });
                    }

                    function rejectAuth() {
                        history.back();
                    }
                </script>
            </body>
            </html>
            """;

    /**
     * 加载登录页面模板
     *
     * @return 登录页面 HTML
     */
    public String loadLoginTemplate() {
        // 1. 尝试从 classpath 加载外部模板
        String customTemplate = loadTemplateFromPath(properties.getServer().getLoginTemplatePath());
        if (customTemplate != null) {
            log.info("使用自定义登录页面模板: {}", properties.getServer().getLoginTemplatePath());
            return customTemplate;
        }

        // 2. 使用默认模板
        return DEFAULT_LOGIN_TEMPLATE;
    }

    /**
     * 加载授权确认页面模板
     *
     * @return 授权确认页面 HTML
     */
    public String loadConfirmTemplate() {
        // 1. 尝试从 classpath 加载外部模板
        String customTemplate = loadTemplateFromPath(properties.getServer().getConfirmTemplatePath());
        if (customTemplate != null) {
            log.info("使用自定义授权确认页面模板: {}", properties.getServer().getConfirmTemplatePath());
            return customTemplate;
        }

        // 2. 使用默认模板
        return DEFAULT_CONFIRM_TEMPLATE;
    }

    /**
     * 从指定路径加载模板文件
     *
     * @param path 模板路径（classpath 下的相对路径）
     * @return 模板内容，如果文件不存在则返回 null
     */
    private String loadTemplateFromPath(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }

        try {
            ClassPathResource resource = new ClassPathResource(path);
            if (resource.exists()) {
                byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.warn("加载模板文件失败: {}", path, e);
        }

        return null;
    }

    /**
     * 替换模板中的占位符
     *
     * @param template 模板内容
     * @param clientId 客户端 ID
     * @param scopes 授权范围
     * @return 替换后的 HTML
     */
    public String fillConfirmTemplate(String template, String clientId, String scopes) {
        return template
                .replace("{{CLIENT_ID}}", clientId)
                .replace("{{SCOPES}}", scopes);
    }
}
