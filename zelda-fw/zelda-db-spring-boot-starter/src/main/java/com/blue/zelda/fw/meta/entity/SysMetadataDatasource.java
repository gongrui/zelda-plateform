package com.blue.zelda.fw.meta.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 元数据数据源实体类
 *
 * <p>存储数据源的配置信息，包括主表、查询配置、参数规格等。
 * 通过数据源配置可以实现动态 SQL 查询。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Data
@Table("sys_metadata_datasource")
public class SysMetadataDatasource {
    /**
     * 主键 ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 数据源编码，唯一标识
     */
    private String dataCode;

    /**
     * 主表配置 JSON，包含表名、别名、主键等信息
     */
    private String tableMain;

    /**
     * 查询配置 JSON，包含 SELECT、JOIN、WHERE、GROUP BY、ORDER BY 等
     */
    private String queryConfig;

    /**
     * 参数规格 JSON，描述前端可传递的参数
     */
    private String paramSpec;

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