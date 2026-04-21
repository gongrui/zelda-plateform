package com.blue.zelda.fw.meta.util;

import com.blue.zelda.fw.core.util.JacksonUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 到元数据配置转换器
 *
 * <p>将原生 SQL 语句转换为元数据配置 JSON，用于初始化数据源配置。
 * 支持解析 SELECT、FROM、JOIN、WHERE、GROUP BY、HAVING、ORDER BY 等 SQL 子句。</p>
 *
 * @author zelda
 * @since 1.0.0
 */
public class SqlToMetadataConfigConverter {

    /**
     * 将 SQL 转换为元数据配置 JSON 字符串
     *
     * @param sql 原生 SQL 语句
     * @return 元数据配置 JSON 字符串
     */
    public static String convert(String sql) {
        sql = sql.replaceAll("\\s+", " ").trim();
        Map<String, Object> json = new LinkedHashMap<>();

        List<String> selectFields = extractSelect(sql);
        json.put("select_fields", selectFields);

        String mainTable = extractMainTable(sql);
        json.put("table_main", mainTable.split(" ")[0]);
        if (mainTable.contains(" ")) {
            json.put("main_alias", mainTable.split(" ")[1]);
        }

        List<Map<String, Object>> joins = extractJoins(sql);
        if (!joins.isEmpty()) json.put("join_list", joins);

        String where = extractWhere(sql);
        if (where != null) json.put("where_raw", where);

        List<String> groupBy = extractGroupBy(sql);
        if (!groupBy.isEmpty()) json.put("group_by", groupBy);

        String having = extractHaving(sql);
        if (having != null) json.put("having_raw", having);

        List<String> orderBy = extractOrderBy(sql);
        if (!orderBy.isEmpty()) json.put("order_by", orderBy);

        return JacksonUtils.toJson(json);
    }

    /**
     * 提取 SELECT 字段列表
     *
     * @param sql SQL 语句
     * @return 字段列表
     */
    private static List<String> extractSelect(String sql) {
        Pattern p = Pattern.compile("SELECT\\s+(.*?)\\s+FROM", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        List<String> list = new ArrayList<>();
        if (m.find()) {
            String[] arr = m.group(1).split(",");
            for (String s : arr) list.add(s.trim());
        }
        return list;
    }

    /**
     * 提取主表及别名
     *
     * @param sql SQL 语句
     * @return 主表（包含别名）
     */
    private static String extractMainTable(String sql) {
        Pattern p = Pattern.compile("FROM\\s+(.*?)\\s+(LEFT|INNER|JOIN|WHERE|GROUP|ORDER|$)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        if (m.find()) return m.group(1).trim();
        return "";
    }

    /**
     * 提取 JOIN 列表
     *
     * @param sql SQL 语句
     * @return JOIN 配置列表
     */
    private static List<Map<String, Object>> extractJoins(String sql) {
        List<Map<String, Object>> list = new ArrayList<>();
        Pattern p = Pattern.compile("(LEFT JOIN|INNER JOIN|JOIN)\\s+(.*?)\\s+ON\\s+(.*?)(?=LEFT|INNER|WHERE|GROUP|ORDER|$)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        while (m.find()) {
            Map<String, Object> j = new LinkedHashMap<>();
            j.put("join_type", m.group(1).trim());
            j.put("join_table", m.group(2).trim().split(" ")[0]);
            j.put("join_alias", m.group(2).trim().split(" ").length > 1 ? m.group(2).trim().split(" ")[1] : "");
            j.put("on_condition", m.group(3).trim());
            list.add(j);
        }
        return list;
    }

    /**
     * 提取 WHERE 条件
     *
     * @param sql SQL 语句
     * @return WHERE 条件，如果没有则返回 null
     */
    private static String extractWhere(String sql) {
        Pattern p = Pattern.compile("WHERE\\s+(.*?)(GROUP|ORDER|$)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        if (m.find()) return m.group(1).replaceAll("GROUP BY.*|ORDER BY.*", "").trim();
        return null;
    }

    /**
     * 提取 GROUP BY 字段列表
     *
     * @param sql SQL 语句
     * @return 分组字段列表
     */
    private static List<String> extractGroupBy(String sql) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("GROUP BY\\s+(.*?)(HAVING|ORDER|$)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        if (m.find()) {
            String[] arr = m.group(1).split(",");
            for (String s : arr) list.add(s.trim());
        }
        return list;
    }

    /**
     * 提取 HAVING 条件
     *
     * @param sql SQL 语句
     * @return HAVING 条件，如果没有则返回 null
     */
    private static String extractHaving(String sql) {
        Pattern p = Pattern.compile("HAVING\\s+(.*?)(ORDER|$)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        if (m.find()) return m.group(1).trim();
        return null;
    }

    /**
     * 提取 ORDER BY 字段列表
     *
     * @param sql SQL 语句
     * @return 排序字段列表
     */
    private static List<String> extractOrderBy(String sql) {
        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("ORDER BY\\s+(.*)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        if (m.find()) {
            String[] arr = m.group(1).split(",");
            for (String s : arr) list.add(s.trim());
        }
        return list;
    }
}
