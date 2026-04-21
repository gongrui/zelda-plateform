package com.blue.zelda.fw.core.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.ibatis.annotations.Update;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * 基础实体类
 * <p>包含ID、创建人、创建时间等基础字段，所有业务实体应继承此类</p>
 *
 * <h3>字段说明</h3>
 * <ul>
 *   <li>id - 主键ID（雪花算法生成）</li>
 *   <li>createBy - 创建人ID（自动填充）</li>
 *   <li>createName - 创建人名称（自动填充）</li>
 *   <li>createTime - 创建时间（自动填充）</li>
 * </ul>
 *
 * @param <T> 主键类型（通常为Long）
 * @author gongrui
 * @since 1.0.0
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Entity <T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // ==================== 字段名常量（Java属性名） ====================

    /**
     * 租户ID属性名
     */
    public static final String TENANT_ID = "tenantId";

    /**
     * ID属性名
     */
    public static final String FIELD_ID = "id";

    /**
     * 创建时间属性名
     */
    public static final String CREATE_TIME = "createTime";

    /**
     * 创建人ID属性名
     */
    public static final String CREATE_USER = "createBy";

    /**
     * 创建人名称属性名
     */
    public static final String CREATE_USER_NAME = "createName";

    // ==================== 列名常量（数据库列名） ====================

    /**
     * 创建时间列名
     */
    public static final String CREATE_TIME_COLUMN = "create_time";

    /**
     * 创建人ID列名
     */
    public static final String CREATE_USER_COLUMN = "create_by";

    /**
     * 创建人名称列名
     */
    public static final String CREATE_USER_NAME_COLUMN = "create_name";

    @Schema(description = "ID")
    @Id(value = FIELD_ID, keyType = KeyType.Generator)
    @Column(comment = "主键ID")
    @NotNull(message = "ID不能为空", groups = Update.class)
    private T id;

    /**
     * 创建人ID
     */
    @Column(value = CREATE_USER_COLUMN)
    @Schema(description = "创建人ID")
    private T createBy;

    /**
     * 创建人名称
     */
    @Column(value = CREATE_USER_NAME_COLUMN)
    @Schema(description = "创建人名称")
    private String createName;

    /**
     * 创建时间
     */
    @Column(value = CREATE_TIME_COLUMN)
    @Schema(description = "创建时间")
    private Instant createTime;

}
