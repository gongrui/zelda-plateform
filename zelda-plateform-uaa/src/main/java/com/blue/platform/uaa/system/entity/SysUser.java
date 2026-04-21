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
 * 系统用户表
 *
 * @author gongrui
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_user")
public class SysUser {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "姓名/昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "部门ID")
    @Column(value = "dept_id")
    private String deptId;

    @Schema(description = "角色ID")
    @Column(value = "role_id")
    private String roleId;

    @Schema(description = "状态(true=启用;false=禁用)")
    private Boolean status;

    @Schema(description = "删除标记")
    private Boolean deleted;

    @Schema(description = "创建时间")
    @Column(value = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @Column(value = "update_time")
    private LocalDateTime updateTime;
}
