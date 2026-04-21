package com.blue.zelda.fw.meta.util;

import com.blue.zelda.fw.core.util.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.query.RawQueryCondition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 元数据查询构建器
 *
 * <p>基于 MyBatis-Flex 1.11.6 实现动态 SQL 构建，支持以下功能：</p>
 * <ul>
 *   <li>从 table_main JSON 解析真实表名</li>
 *   <li>前端动态参数（排除分页、排序参数）</li>
 *   <li>List 参数自动转 IN 查询</li>
 *   <li>前端传参排序（sortField + sortOrder）</li>
 *   <li>配置文件排序（order_by）</li>
 * </ul>
 *
 * @author zelda
 * @since 1.0.0
 */
public class MetadataQueryBuilder {

    /**
     * 构建动态查询 QueryWrapper
     *
     * @param tableMain       主表配置 JSON（格式：{"mainTable":"xxx","pk":"id"}）
     * @param queryConfigJson 查询配置 JSON
     * @param frontParams     前端动态参数
     * @return MyBatis-Flex QueryWrapper
     */
    public static QueryWrapper build(String tableMain,
                                     String queryConfigJson,
                                     Map<String, Object> frontParams) {
        QueryWrapper qw = QueryWrapper.create();
        Map<String, Object> config = JacksonUtils.readValue(queryConfigJson, new TypeReference<Map<String, Object>>() {});

        // 1. 设置主表
        buildFrom(qw, tableMain, config);

        // 2. 设置查询字段
        buildSelect(qw, config);

        // 3. 构建关联表
        buildJoins(qw, config);

        // 4. 构建 WHERE 条件
        buildWhere(qw, config, frontParams);

        // 5. 构建 GROUP BY
        buildGroupBy(qw, config);

        // 6. 构建 HAVING 条件
        buildHaving(qw, config);

        // 7. 构建 ORDER BY（优先配置文件，其次前端传参）
        buildOrderBy(qw, config, frontParams);

        return qw;
    }

    // ====================== 私有构建方法 ======================

    /**
     * 设置主表和别名
     *
     * @param qw            查询包装器
     * @param tableMainJson  主表配置 JSON
     * @param config        查询配置
     */
    private static void buildFrom(QueryWrapper qw, String tableMainJson, Map<String, Object> config) {
        // 解析 tableMain JSON，拿到真实表名
        Map<String, Object> tableMainObj = JacksonUtils.readValue(tableMainJson, new TypeReference<Map<String, Object>>() {});
        String realTableName = (String) tableMainObj.get("mainTable");

        qw.from(realTableName);
        if (config.containsKey("main_alias")) {
            qw.as((String) config.get("main_alias"));
        }
    }

    /**
     * 设置查询字段
     *
     * @param qw     查询包装器
     * @param config 查询配置
     */
    private static void buildSelect(QueryWrapper qw, Map<String, Object> config) {
        List<String> selectFields = (List<String>) config.get("select_fields");
        if (!CollectionUtils.isEmpty(selectFields)) {
            qw.select(selectFields.toArray(new String[0]));
        }
    }

    /**
     * 构建关联表
     *
     * <p>支持 LEFT JOIN 和 INNER JOIN 两种关联方式。</p>
     *
     * @param qw     查询包装器
     * @param config 查询配置
     */
    private static void buildJoins(QueryWrapper qw, Map<String, Object> config) {
        if (!config.containsKey("join_list")) {
            return;
        }

        List<Map<String, Object>> joinConfigs = (List<Map<String, Object>>) config.get("join_list");
        if (CollectionUtils.isEmpty(joinConfigs)) {
            return;
        }

        for (Map<String, Object> joinConfig : joinConfigs) {
            String type = (String) joinConfig.get("join_type");
            String table = (String) joinConfig.get("join_table");
            String alias = (String) joinConfig.get("join_alias");
            String on = (String) joinConfig.get("on_condition");

            switch (type.toUpperCase()) {
                case "LEFT JOIN" -> qw.leftJoin(table).as(alias).on(on);
                case "INNER JOIN" -> qw.innerJoin(table).as(alias).on(on);
                default -> throw new IllegalArgumentException("不支持的关联类型: " + type);
            }
        }
    }

    /**
     * 构建 WHERE 条件（原生 SQL + 动态参数）
     *
     * <p>自动排除：分页参数（pageNum/pageSize）、排序参数（sortField/sortOrder/orderBy）。
     * List 类型参数自动转为 IN 查询。</p>
     *
     * @param qw          查询包装器
     * @param config      查询配置
     * @param frontParams 前端动态参数
     */
    private static void buildWhere(QueryWrapper qw, Map<String, Object> config, Map<String, Object> frontParams) {
        // 1. 原生 WHERE SQL
        String whereRaw = (String) config.get("where_raw");
        if (StringUtils.hasText(whereRaw)) {
            qw.and(whereRaw);
        }

        // 2. 前端动态参数
        if (frontParams == null || frontParams.isEmpty()) {
            return;
        }

        frontParams.forEach((k, v) -> {
            // 排除不需要的参数
            if (!"pageNum".equals(k) && !"pageSize".equals(k)
                    && !"sortField".equals(k) && !"sortOrder".equals(k)
                    && !"orderBy".equals(k)
                    && Objects.nonNull(v)
                    && StringUtils.hasText(String.valueOf(v))) {

                // 处理 List 参数：自动转 IN 查询
                if (v instanceof List) {
                    List<?> list = (List<?>) v;
                    if (!list.isEmpty()) {
                        qw.in(k, list);
                    }
                } else {
                    // 普通参数：等于查询
                    qw.eq(k, v);
                }
            }
        });
    }

    /**
     * 构建 GROUP BY
     *
     * @param qw     查询包装器
     * @param config 查询配置
     */
    private static void buildGroupBy(QueryWrapper qw, Map<String, Object> config) {
        if (!config.containsKey("group_by")) {
            return;
        }

        List<String> groupByFields = (List<String>) config.get("group_by");
        if (!CollectionUtils.isEmpty(groupByFields)) {
            qw.groupBy(groupByFields.toArray(new String[0]));
        }
    }

    /**
     * 构建 HAVING 条件
     *
     * @param qw     查询包装器
     * @param config 查询配置
     */
    private static void buildHaving(QueryWrapper qw, Map<String, Object> config) {
        if (!config.containsKey("having_raw")) {
            return;
        }

        String having = (String) config.get("having_raw");
        if (StringUtils.hasText(having)) {
            qw.having(new RawQueryCondition(having));
        }
    }

    /**
     * 构建 ORDER BY
     *
     * <p>优先级：配置文件 order_by > 前端传参 sortField + sortOrder</p>
     *
     * @param qw          查询包装器
     * @param config      查询配置
     * @param frontParams 前端动态参数
     */
    private static void buildOrderBy(QueryWrapper qw, Map<String, Object> config, Map<String, Object> frontParams) {
        // 1. 优先使用配置文件里的 order_by
        if (config.containsKey("order_by")) {
            List<String> orderBys = (List<String>) config.get("order_by");
            if (!CollectionUtils.isEmpty(orderBys)) {
                orderBys.forEach(qw::orderBy);
                return;
            }
        }

        // 2. 其次使用前端传的 sortField + sortOrder
        if (frontParams == null || !frontParams.containsKey("sortField")) {
            return;
        }

        String sortField = String.valueOf(frontParams.get("sortField"));
        if (!StringUtils.hasText(sortField)) {
            return;
        }

        // 获取排序方向，默认 asc
        String sortOrder = "asc";
        if (frontParams.containsKey("sortOrder")) {
            String order = String.valueOf(frontParams.get("sortOrder"));
            if ("desc".equalsIgnoreCase(order)) {
                sortOrder = "desc";
            }
        }

        // 【方案一】直接传字符串："created_at desc" 或 "created_at asc"
        qw.orderBy(sortField + " " + sortOrder);
    }
}