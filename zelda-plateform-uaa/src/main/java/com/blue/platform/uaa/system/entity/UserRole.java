package com.blue.platform.uaa.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 用户 - 角色关联表（支持一个用户拥有多个角色）
 *
 * @author gongrui
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_user_role")
@Schema(name = "UserRole", description = "用户角色关联")
public class UserRole {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键 ID")
    private String id;

    @Schema(description = "用户 ID")
    @Column(value = "user_id")
    private String userId;

    @Schema(description = "角色 ID")
    @Column(value = "role_id")
    private String roleId;

    @Schema(description = "创建时间")
    @Column(value = "create_time")
    private LocalDateTime createTime;
}
