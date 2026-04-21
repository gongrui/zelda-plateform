package com.blue.zelda.fw.security.context;

import com.blue.zelda.fw.core.entity.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 认证详情实体
 *
 * <p>存储完整的用户认证信息，包括基本信息、权限列表等</p>
 *
 * @author gongrui
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationDetails {

    /**
     * 租户编码
     */
    private String tenantCode;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户类型
     */
    private UserType userType;

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 功能权限列表
     */
    private List<String> funcPermissions;

    /**
     * 角色权限列表
     */
    private List<String> rolePermissions;

    /**
     * 数据权限
     */
    private DataPermission dataPermission;

    /**
     * 是否为匿名用户
     */
    private boolean anonymous;

    /**
     * 匿名用户详情
     */
    public static AuthenticationDetails anonymous() {
        return AuthenticationDetails.builder()
                .anonymous(true)
                .userId(-1L)
                .nickname("anonymous")
                .build();
    }

    /**
     * 数据权限定义
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPermission {
        /**
         * 权限类型：ALL, SELF, DEPT, DEPT_AND_CHILD, CUSTOM
         */
        private String type;

        /**
         * 自定义权限 - 允许访问的部门ID列表
         */
        private List<Long> deptIds;
    }
}
