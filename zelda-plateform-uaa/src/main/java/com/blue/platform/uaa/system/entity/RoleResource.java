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
 * 角色 - 资源关联表（支持一个角色拥有多个资源权限）
 *
 * @author gongrui
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_role_resource")
@Schema(name = "RoleResource", description = "角色资源关联")
public class RoleResource {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键 ID")
    private String id;

    @Schema(description = "角色 ID")
    @Column(value = "role_id")
    private String roleId;

    @Schema(description = "资源 ID")
    @Column(value = "resource_id")
    private String resourceId;

    @Schema(description = "创建时间")
    @Column(value = "create_time")
    private LocalDateTime createTime;
}
