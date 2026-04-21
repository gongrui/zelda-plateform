package com.blue.platform.uaa.system.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 岗位
 *
 * @author gongrui
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Table(value="sys_resource")
@Schema(name = "Resource", description = "系统资源")
public class Resource {

    @Schema(description = "归属应用")
    private String clientId;

    @Schema(description = "权限编码")
    @Column(value = "permission")
    private String permission;

    @Schema(description = "名称")
    private String title;

    @Schema(description = "父级菜单ID")
    private Long parentId;

//    @Schema(description = "类型")
//    private ResourceType type;

    @Schema(description = "排序")
    private Integer sequence;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "组件")
    private String component;

    @Schema(description = "页面缓存，开启后页面会缓存，不会重新加载，仅在标签页启用时有效")
    private Boolean keepAlive;

    @Schema(description = "公共资源（无需分配所有人可访问）")
    private Boolean shared;

    @Schema(description = "是否可见")
    private Boolean visible;

    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "路由元信息(JSON)")
    @Column(typeHandler = com.mybatisflex.core.handler.JacksonTypeHandler.class) // Flex 自带 JSON 处理器
    private Map<String, Object> meta;
}
