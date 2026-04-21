package com.blue.demo.entity;

import com.mybatis.flex.annotation.KeyValue;
import com.mybatis.flex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table("t_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单 ID
     */
    @KeyValue
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 订单金额
     */
    private Double amount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
}
