package com.blue.zelda.fw.security.holder;

import com.blue.zelda.fw.core.entity.enums.UserType;
import com.blue.zelda.fw.core.threadlocal.ThreadLocalHolder;
import com.blue.zelda.fw.security.context.AuthenticationDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

/**
 * 认证上下文持有器
 *
 * <p>实现 AuthenticationContext 接口，提供静态方法访问当前用户信息</p>
 *
 * <p>使用 ThreadLocalHolder 存储用户上下文，支持跨线程传递</p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 获取当前用户ID
 * Long userId = AuthenticationContextHolder.userId();
 *
 * // 获取当前用户昵称
 * String nickname = AuthenticationContextHolder.nickname();
 *
 * // 获取完整认证详情
 * AuthenticationDetails details = AuthenticationContextHolder.getContext();
 *
 * // 在异步线程中也能获取（配合 TransmittableThreadLocal）
 * CompletableFuture.runAsync(() -> {
 *     Long userId = AuthenticationContextHolder.userId();
 * });
 * }</pre>
 *
 * @author gongrui
 * @since 1.0.0
 * @see com.blue.zelda.fw.core.security.AuthenticationContext
 * @see com.blue.zelda.fw.core.threadlocal.ThreadLocalHolder
 */
public interface AuthenticationContextHolder {

    // ==================== ThreadLocal Key ====================

    String KEY_AUTH_DETAILS = "auth_details";
    String KEY_USER_ID = "auth_user_id";
    String KEY_USER_TYPE = "auth_user_type";
    String KEY_TENANT_CODE = "auth_tenant_code";
    String KEY_CLIENT_ID = "auth_client_id";
    String KEY_NICKNAME = "auth_nickname";
    String KEY_MOBILE = "auth_mobile";
    String KEY_ANONYMOUS = "auth_anonymous";

    // ==================== 静态便捷方法 ====================

    /**
     * 获取当前用户ID
     */
    static Long userId() {
        return ThreadLocalHolder.getLong(KEY_USER_ID);
    }

    /**
     * 获取当前租户编码
     */
    static String tenantCode() {
        return ThreadLocalHolder.getString(KEY_TENANT_CODE);
    }

    /**
     * 获取用户类型
     */
    static UserType userType() {
        Object value = ThreadLocalHolder.get(KEY_USER_TYPE);
        if (value instanceof UserType ut) {
            return ut;
        }
        if (value instanceof String s) {
            try {
                return UserType.valueOf(s);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取客户端ID
     */
    static String clientId() {
        return ThreadLocalHolder.getString(KEY_CLIENT_ID);
    }

    /**
     * 获取用户昵称
     */
    static String nickname() {
        return ThreadLocalHolder.getString(KEY_NICKNAME);
    }

    /**
     * 获取手机号
     */
    static String mobile() {
        return ThreadLocalHolder.getString(KEY_MOBILE);
    }

    /**
     * 是否为匿名用户
     */
    static boolean anonymous() {
        Boolean value = ThreadLocalHolder.getBoolean(KEY_ANONYMOUS);
        return Boolean.TRUE.equals(value);
    }

    /**
     * 获取功能权限列表
     */
    @SuppressWarnings("unchecked")
    static List<String> funcPermissionList() {
        AuthenticationDetails details = getContext();
        return details != null ? details.getFuncPermissions() : null;
    }

    /**
     * 获取角色权限列表
     */
    @SuppressWarnings("unchecked")
    static List<String> rolePermissionList() {
        AuthenticationDetails details = getContext();
        return details != null ? details.getRolePermissions() : null;
    }

    /**
     * 获取数据权限
     */
    static AuthenticationDetails.DataPermission dataPermission() {
        AuthenticationDetails details = getContext();
        return details != null ? details.getDataPermission() : null;
    }

    /**
     * 获取完整认证详情
     */
    static AuthenticationDetails getContext() {
        return ThreadLocalHolder.getAs(KEY_AUTH_DETAILS, AuthenticationDetails.class);
    }

    // ==================== 设置上下文 ====================

    /**
     * 设置认证详情
     */
    static void setContext(AuthenticationDetails details) {
        if (details == null) {
            clear();
            return;
        }
        ThreadLocalHolder.set(KEY_AUTH_DETAILS, details);
        ThreadLocalHolder.set(KEY_USER_ID, details.getUserId());
        ThreadLocalHolder.set(KEY_USER_TYPE, details.getUserType());
        ThreadLocalHolder.set(KEY_TENANT_CODE, details.getTenantCode());
        ThreadLocalHolder.set(KEY_CLIENT_ID, details.getClientId());
        ThreadLocalHolder.set(KEY_NICKNAME, details.getNickname());
        ThreadLocalHolder.set(KEY_MOBILE, details.getMobile());
        ThreadLocalHolder.set(KEY_ANONYMOUS, details.isAnonymous());
    }

    /**
     * 设置当前用户ID
     */
    static void setUserId(Long userId) {
        ThreadLocalHolder.set(KEY_USER_ID, userId);
    }

    /**
     * 设置租户编码
     */
    static void setTenantCode(String tenantCode) {
        ThreadLocalHolder.set(KEY_TENANT_CODE, tenantCode);
    }

    /**
     * 清除上下文
     */
    static void clear() {
        ThreadLocalHolder.remove(KEY_AUTH_DETAILS);
        ThreadLocalHolder.remove(KEY_USER_ID);
        ThreadLocalHolder.remove(KEY_USER_TYPE);
        ThreadLocalHolder.remove(KEY_TENANT_CODE);
        ThreadLocalHolder.remove(KEY_CLIENT_ID);
        ThreadLocalHolder.remove(KEY_NICKNAME);
        ThreadLocalHolder.remove(KEY_MOBILE);
        ThreadLocalHolder.remove(KEY_ANONYMOUS);
    }

    // ==================== 快捷实现接口 ====================

    /**
     * 获取当前 HttpServletRequest
     */
    static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    /**
     * 创建一个匿名用户的认证详情
     */
    static AuthenticationDetails anonymousContext() {
        return AuthenticationDetails.anonymous();
    }
}
