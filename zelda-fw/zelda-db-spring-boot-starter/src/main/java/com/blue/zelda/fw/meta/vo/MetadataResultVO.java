package com.blue.zelda.fw.meta.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 元数据查询结果 VO
 *
 * <p>封装元数据查询的返回结果，包括视图配置、查询参数、数据记录和分页信息。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
@Data
@Builder
public class MetadataResultVO {
    /**
     * 视图配置，包括基础配置、列配置、操作配置、过滤配置等
     */
    private Map<String, Object> viewConfig;

    /**
     * 查询参数，即前端传递的原始参数
     */
    private Map<String, Object> queryParams;

    /**
     * 数据记录列表
     */
    private List<Map<String, Object>> records;

    /**
     * 分页信息
     */
    private PageInfo page;

    /**
     * 分页信息
     */
    @Data
    @Builder
    public static class PageInfo {
        /**
         * 当前页码
         */
        private long pageNum;

        /**
         * 每页大小
         */
        private long pageSize;

        /**
         * 总记录数
         */
        private long total;

        /**
         * 总页数
         */
        private long pages;
    }
}