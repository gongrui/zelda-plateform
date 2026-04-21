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

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_role")
public class Role {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键 ID")
    private String id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "超级角色")
    @Column(value = "super")
    private Boolean superRole;

    @Schema(description = "租户描述")
    private String description;

    @Schema(description = "是否只读")
    private Boolean readonly;

    @Schema(description = "状态(true=启用;false=禁用)")
    private Boolean status;

//    @Schema(description = "权限类型")
//    private DataScopeType scopeType;
}
