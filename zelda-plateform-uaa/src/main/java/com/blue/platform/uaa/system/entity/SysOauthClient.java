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
 * OAuth2 客户端表
 *
 * @author gongrui
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "sys_oauth_client")
public class SysOauthClient {

    @Id(keyType = KeyType.Generator, value = "snowFlakeId")
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "客户端ID")
    private String clientId;

    @Schema(description = "客户端密钥")
    private String clientSecret;

    @Schema(description = "客户端名称")
    private String clientName;

    @Schema(description = "客户端描述")
    private String description;

    @Schema(description = "重定向URI（多个用逗号分隔）")
    @Column(value = "redirect_uris")
    private String redirectUris;

    @Schema(description = "授权类型（多个用逗号分隔: authorization_code,password,client_credentials,refresh_token）")
    @Column(value = "authorized_grant_types")
    private String authorizedGrantTypes;

    @Schema(description = "作用域（多个用逗号分隔）")
    private String scopes;

    @Schema(description = "访问令牌有效期（秒）")
    @Column(value = "access_token_validity")
    private Integer accessTokenValidity;

    @Schema(description = "刷新令牌有效期（秒）")
    @Column(value = "refresh_token_validity")
    private Integer refreshTokenValidity;

    @Schema(description = "客户端类型: none(公开客户端), confidential(机密客户端)")
    @Column(value = "client_type")
    private String clientType;

    @Schema(description = "自动确认授权（true=无需用户确认）")
    @Column(value = "is_auto_confirm")
    private Boolean autoConfirm;

    @Schema(description = "状态(true=启用;false=禁用)")
    private Boolean status;

    @Schema(description = "创建时间")
    @Column(value = "create_time")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @Column(value = "update_time")
    private LocalDateTime updateTime;

    @Schema(description = "删除标记")
    private Boolean deleted;
}
