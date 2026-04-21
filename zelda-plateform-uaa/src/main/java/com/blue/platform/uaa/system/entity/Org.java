package com.blue.platform.uaa.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.List;

/**
 * 组织
 *
 * @author gongrui
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_org")
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Org", description = "组织")
public class Org {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键 ID")
    private String id;

    @Schema(description = "标题")
    @Column(value = "label")
    private String label;

    @Schema(description = "树形结构路径")
    @Column(value = "tree_path", typeHandler = JacksonTypeHandler.class)
    private List<String> treePath;

    @Schema(description = "父ID")
    @Column(value = "parent_id")
    private String parentId;

    @Schema(description = "排序号")
    @Column(value = "sequence")
    private Integer sequence;

    @Schema(description = "电话")
    @Column(value = "tel")
    private String tel;


    @Schema(description = "简称")
    @Column(value = "alias")
    private String alias;

    @Schema(description = "状态")
    @Column(value = "status")
    private Boolean status;

    @Schema(description = "描述")
    @Column(value = "description")
    private String description;
}
