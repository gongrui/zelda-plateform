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
 * 应用表 - 支持多应用/多租户管理
 *
 * @author gongrui
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_app")
@Schema(name = "App", description = "应用")
public class App {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键 ID")
    private String id;

    @Schema(description = "应用编码（唯一标识）")
    private String code;

    @Schema(description = "应用名称")
    private String name;

    @Schema(description = "应用描述")
    private String description;

    @Schema(description = "应用图标 URL")
    private String icon;

    @Schema(description = "状态 (true=启用;false=禁用)")
    private Boolean status;

    @Schema(description = "排序号")
    private Integer sequence;

    @Schema(description = "创建时间")
    @Column(value = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @Column(value = "update_time")
    private LocalDateTime updateTime;

    @Schema(description = "删除标记")
    private Boolean deleted;
}
