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
import java.util.List;

/**
 * 部门表 - 组织架构中的部门
 *
 * @author gongrui
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_dept")
@Schema(name = "Dept", description = "部门")
public class Dept {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键 ID")
    private String id;

    @Schema(description = "应用 ID")
    @Column(value = "app_id")
    private String appId;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "部门编码")
    private String code;

    @Schema(description = "父部门 ID")
    @Column(value = "parent_id")
    private String parentId;

    @Schema(description = "树形结构路径")
    @Column(value = "tree_path")
    private String treePath;

    @Schema(description = "排序号")
    private Integer sequence;

    @Schema(description = "负责人 ID")
    @Column(value = "leader_id")
    private String leaderId;

    @Schema(description = "负责人姓名")
    @Column(value = "leader_name")
    private String leaderName;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态 (true=启用;false=禁用)")
    private Boolean status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "创建时间")
    @Column(value = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @Column(value = "update_time")
    private LocalDateTime updateTime;

    @Schema(description = "删除标记")
    private Boolean deleted;

    /**
     * 子部门列表（非数据库字段，用于树形结构展示）
     */
    @io.swagger.v3.oas.annotations.media.Schema(description = "子部门列表")
    @com.mybatisflex.annotation.IgnoreInsert
    @com.mybatisflex.annotation.IgnoreUpdate
    private List<Dept> children;
}
