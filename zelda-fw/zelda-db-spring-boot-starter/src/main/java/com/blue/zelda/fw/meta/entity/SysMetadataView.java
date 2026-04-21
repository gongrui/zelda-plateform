package com.blue.zelda.fw.meta.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 元数据视图实体类
 *
 * <p>存储视图的配置信息，包括基础配置、列配置、操作配置、过滤配置等。
 * 视图配置用于定义前端页面的展示方式和交互行为。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Data
@Table("sys_metadata_view")
public class SysMetadataView {
    /**
     * 主键 ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 视图编码，唯一标识
     */
    private String viewCode;

    /**
     * 数据源编码，关联到 {@link SysMetadataDatasource}
     */
    private String dsCode;

    /**
     * 视图名称
     */
    private String name;

    /**
     * 基础配置 JSON，包含分页、排序等配置
     */
    private String baseConfig;

    /**
     * 列配置 JSON，定义表格列的展示方式
     */
    private String columnConfig;

    /**
     * 操作配置 JSON，定义行操作按钮
     */
    private String actionConfig;

    /**
     * 过滤配置 JSON，定义查询条件
     */
    private String filterConfig;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 删除时间
     */
    private LocalDateTime deletedAt;
}
