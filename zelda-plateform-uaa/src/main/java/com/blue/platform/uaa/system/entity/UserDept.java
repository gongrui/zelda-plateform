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
 * 用户 - 部门关联表（支持一个用户属于多个部门）
 *
 * @author gongrui
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_user_dept")
@Schema(name = "UserDept", description = "用户部门关联")
public class UserDept {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键 ID")
    private String id;

    @Schema(description = "用户 ID")
    @Column(value = "user_id")
    private String userId;

    @Schema(description = "部门 ID")
    @Column(value = "dept_id")
    private String deptId;

    @Schema(description = "是否为主部门 (true=是;false=否)")
    @Column(value = "is_primary")
    private Boolean primary;

    @Schema(description = "创建时间")
    @Column(value = "create_time")
    private LocalDateTime createTime;
}
