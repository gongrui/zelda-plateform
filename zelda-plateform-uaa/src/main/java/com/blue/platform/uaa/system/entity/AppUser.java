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
 * 应用 - 用户关联表（支持用户在多个应用中拥有不同权限）
 *
 * @author gongrui
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_app_user")
@Schema(name = "AppUser", description = "应用用户关联")
public class AppUser {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键 ID")
    private String id;

    @Schema(description = "应用 ID")
    @Column(value = "app_id")
    private String appId;

    @Schema(description = "用户 ID")
    @Column(value = "user_id")
    private String userId;

    @Schema(description = "状态 (true=启用;false=禁用)")
    private Boolean status;

    @Schema(description = "创建时间")
    @Column(value = "create_time")
    private LocalDateTime createTime;
}
