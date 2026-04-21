//package com.blue.zelda.fw.security.filter;
//
//import cn.hutool.core.util.StrUtil;
//import com.blue.zelda.fw.core.entity.enums.UserType;
//import com.blue.zelda.fw.security.config.AuthenticationProperties;
//import com.blue.zelda.fw.security.context.AuthenticationDetails;
//import com.blue.zelda.fw.security.holder.AuthenticationContextHolder;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
///**
// * 认证上下文过滤器
// *
// * <p>从 HTTP Header 中提取用户认证信息，设置到 ThreadLocalHolder</p>
// *
// * <p>支持的 Header：</p>
// * <ul>
// *   <li>X-User-Id - 用户ID</li>
// *   <li>X-Tenant-Code - 租户编码</li>
// *   <li>X-User-Type - 用户类型</li>
// *   <li>X-Nickname - 用户昵称</li>
// *   <li>X-Mobile - 手机号</li>
// *   <li>X-Client-Id - 客户端ID</li>
// * </ul>
// *
// * <p>使用方式：</p>
// * <ol>
// *   <li>Gateway 在请求下游服务时，将用户信息放入 Header</li>
// *   <li>下游服务引入本 starter 后，自动从 Header 读取并设置上下文</li>
// *   <li>业务代码通过 AuthenticationContextHolder.xxx() 获取用户信息</li>
// * </ol>
// *
// * @author gongrui
// * @since 1.0.0
// * @see AuthenticationContextHolder
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//@ConditionalOnProperty(prefix = "zelda.security.authentication", name = "enabled", havingValue = "true", matchIfMissing = true)
//public class AuthenticationContextFilter extends OncePerRequestFilter {
//
//    private final AuthenticationProperties properties;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            // 检查是否在忽略路径中
//            if (isIgnorePath(request)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            // 提取用户信息
//            AuthenticationDetails details = extractAuthenticationDetails(request);
//
//            // 设置到 ThreadLocalHolder
//            AuthenticationContextHolder.setContext(details);
//
//            if (log.isDebugEnabled()) {
//                log.debug("Authentication context set: userId={}, tenant={}, anonymous={}",
//                        details.getUserId(), details.getTenantCode(), details.isAnonymous());
//            }
//
//            filterChain.doFilter(request, response);
//        } finally {
//            // 请求结束后清理
//            AuthenticationContextHolder.clear();
//        }
//    }
//
//    /**
//     * 从请求头中提取认证详情
//     */
//    private AuthenticationDetails extractAuthenticationDetails(HttpServletRequest request) {
//        String userIdStr = getHeader(request, properties.getUserIdHeader());
//        String tenantCode = getHeader(request, properties.getTenantCodeHeader());
//        String userTypeStr = getHeader(request, properties.getUserTypeHeader());
//        String nickname = getHeader(request, properties.getNicknameHeader());
//        String mobile = getHeader(request, properties.getMobileHeader());
//        String clientId = getHeader(request, properties.getClientIdHeader());
//
//        // 解析用户ID
//        Long userId = null;
//        if (StrUtil.isNotBlank(userIdStr)) {
//            try {
//                userId = Long.parseLong(userIdStr);
//            } catch (NumberFormatException e) {
//                log.warn("Invalid userId header: {}", userIdStr);
//            }
//        }
//
//        // 解析用户类型
//        UserType userType = null;
//        if (StrUtil.isNotBlank(userTypeStr)) {
//            try {
//                userType = UserType.valueOf(userTypeStr);
//            } catch (Exception e) {
//                log.warn("Invalid userType header: {}", userTypeStr);
//            }
//        }
//
//        // 判断是否为匿名用户
//        boolean anonymous = userId == null;
//
//        return AuthenticationDetails.builder()
//                .userId(userId != null ? userId : -1L)
//                .tenantCode(tenantCode)
//                .userType(userType)
//                .nickname(StrUtil.blankToDefault(nickname, "unknown"))
//                .mobile(mobile)
//                .clientId(clientId)
//                .anonymous(anonymous)
//                .build();
//    }
//
//    /**
//     * 获取请求头，支持多值
//     */
//    private String getHeader(HttpServletRequest request, String headerName) {
//        if (StrUtil.isBlank(headerName)) {
//            return null;
//        }
//        String value = request.getHeader(headerName);
//        // 优先取第一个值
//        if (value != null && value.contains(",")) {
//            value = value.split(",")[0].trim();
//        }
//        return value;
//    }
//
//    /**
//     * 检查请求路径是否在忽略列表中
//     */
//    private boolean isIgnorePath(HttpServletRequest request) {
//        List<String> ignorePaths = properties.getIgnorePaths();
//        if (ignorePaths == null || ignorePaths.isEmpty()) {
//            return false;
//        }
//
//        String requestPath = request.getRequestURI();
//        String contextPath = request.getContextPath();
//        if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
//            requestPath = requestPath.substring(contextPath.length());
//        }
//
//        return ignorePaths.stream().anyMatch(pattern -> {
//            if (pattern.endsWith("/**")) {
//                String prefix = pattern.substring(0, pattern.length() - 2);
//                return requestPath.startsWith(prefix);
//            }
//            return requestPath.equals(pattern) || requestPath.matches(pattern.replace("**", ".*"));
//        });
//    }
//
//    // ==================== 注册为 Filter ====================
//
//    /**
//     * 配置过滤器注册信息
//     */
//    @Bean
//    public FilterRegistrationBean<AuthenticationContextFilter> authenticationContextFilterRegistration() {
//        FilterRegistrationBean<AuthenticationContextFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new AuthenticationContextFilter(properties));
//        registration.addUrlPatterns("/*");
//        registration.setName("authenticationContextFilter");
//        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
//        return registration;
//    }
//}
