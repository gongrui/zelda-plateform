package com.blue.demo.mapper;

import com.blue.demo.entity.Order;
import com.mybatis.flex.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper 接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
