package com.blue.zelda.fw.meta.mapper;

import com.blue.zelda.fw.meta.entity.SysMetadataDatasource;
import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 元数据数据源 Mapper 接口
 *
 * <p>提供元数据数据源表的数据库访问操作。
 * 继承 MyBatis-Flex 的 {@link BaseMapper}，自动获得增删改查方法。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Mapper
public interface MetadataDatasourceMapper extends BaseMapper<SysMetadataDatasource> {}

