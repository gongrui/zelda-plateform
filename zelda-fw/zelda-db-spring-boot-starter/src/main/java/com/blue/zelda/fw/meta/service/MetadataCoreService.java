package com.blue.zelda.fw.meta.service;

import com.blue.zelda.fw.core.util.JacksonUtils;
import com.blue.zelda.fw.meta.cache.MetaCacheProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.blue.zelda.fw.meta.entity.SysMetadataDatasource;
import com.blue.zelda.fw.meta.entity.SysMetadataView;
import com.blue.zelda.fw.meta.mapper.MetadataDatasourceMapper;
import com.blue.zelda.fw.meta.mapper.MetadataViewMapper;
import com.blue.zelda.fw.meta.util.MetadataQueryBuilder;
import com.blue.zelda.fw.meta.vo.*;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 元数据核心服务类
 *
 * <p>提供元数据查询的核心功能，包括：</p>
 * <ul>
 *   <li>根据视图编码查询数据</li>
 *   <li>动态构建 SQL 查询</li>
 *   <li>支持分页和非分页查询</li>
 *   <li>支持动态参数过滤和排序</li>
 *   <li>元数据缓存管理</li>
 * </ul>
 *
 * @author zelda
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class MetadataCoreService {

    private final MetadataDatasourceMapper datasourceMapper;
    private final MetadataViewMapper viewMapper;

    /**
     * 元数据缓存提供者
     * 用于缓存视图配置和数据源配置，避免频繁查询数据库
     */
    private final MetaCacheProvider cacheProvider;

    /**
     * 根据视图编码查询数据
     *
     * @param viewCode 视图编码
     * @param params 查询参数，包括过滤条件、分页参数、排序参数等
     * @return 查询结果，包含视图配置、查询参数、数据记录、分页信息
     */
    public MetadataResultVO query(String viewCode, Map<String, Object> params) {
        // 从缓存获取视图配置和数据源配置
        SysMetadataView view = cacheProvider.getView(viewCode);
        SysMetadataDatasource ds = cacheProvider.getDs(view.getDsCode());

        // 构建视图配置
        Map<String, Object> baseConfig = JacksonUtils.readValue(view.getBaseConfig(), new TypeReference<Map<String, Object>>() {});
        List<Object> columns = JacksonUtils.readValue(view.getColumnConfig(), new TypeReference<List<Object>>() {});
        List<Object> actions = JacksonUtils.readValue(view.getActionConfig(), new TypeReference<List<Object>>() {});
        List<Object> filters = JacksonUtils.readValue(view.getFilterConfig(), new TypeReference<List<Object>>() {});
        Map<String, Object> viewConf = Map.of(
                "baseConfig", baseConfig,
                "columns", columns,
                "actions", actions,
                "filters", filters
        );

        // 构建动态查询条件
        QueryWrapper qw = MetadataQueryBuilder.build(
                ds.getTableMain(),
                ds.getQueryConfig(),
                params
        );

        // 判断是否禁用分页
        boolean closePage = Boolean.TRUE.equals(baseConfig.get("disable_pagination"));

        List<Map<String, Object>> records;
        MetadataResultVO.PageInfo pageInfo;

        if (closePage) {
            // 非分页查询：查询所有数据
            List<Map> raw = datasourceMapper.selectListByQueryAs(qw, Map.class);
            records = (List) raw;

            pageInfo = MetadataResultVO.PageInfo.builder()
                    .pageNum(1)
                    .pageSize(records.size())
                    .total(records.size())
                    .pages(1)
                    .build();
        } else {
            // 分页查询
            long pageNum = Long.parseLong(String.valueOf(params.getOrDefault("pageNum", 1)));
            long pageSize = Long.parseLong(String.valueOf(params.getOrDefault("pageSize", 20)));

            Page<Map> rawPage = datasourceMapper.paginateAs(pageNum, pageSize, qw, Map.class);
            records = (List) rawPage.getRecords();

            pageInfo = MetadataResultVO.PageInfo.builder()
                    .pageNum(rawPage.getPageNumber())
                    .pageSize(rawPage.getPageSize())
                    .total(rawPage.getTotalRow())
                    .pages(rawPage.getTotalPage())
                    .build();
        }

        return MetadataResultVO.builder()
                .viewConfig(viewConf)
                .queryParams(params)
                .records(records)
                .page(pageInfo)
                .build();
    }
}