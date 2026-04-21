package com.blue.zelda.fw.meta.controller;

import com.blue.zelda.fw.meta.service.MetadataCoreService;
import com.blue.zelda.fw.meta.vo.MetadataResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 元数据查询控制器
 *
 * <p>提供元数据查询的 REST API 接口，支持基于视图编码的动态数据查询。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>根据视图编码查询数据列表</li>
 *   <li>支持分页查询</li>
 *   <li>支持动态参数过滤</li>
 *   <li>支持动态排序</li>
 * </ul>
 *
 * @author zelda
 * @since 1.0.0
 */
@RestController
@RequestMapping("/meta")
@RequiredArgsConstructor
public class MetadataController {

    private final MetadataCoreService coreService;

    /**
     * 根据视图编码查询数据列表
     *
     * @param viewCode 视图编码，用于标识不同的数据视图配置
     * @param params 查询参数，包括过滤条件、分页参数、排序参数等
     * @return 查询结果，包含视图配置、查询参数、数据记录、分页信息
     */
    @PostMapping("/list/{viewCode}")
    public MetadataResultVO list(
            @PathVariable String viewCode,
            @RequestBody(required = false) Map<String, Object> params
    ) {
        if (params == null) params = Map.of();
        return coreService.query(viewCode, params);
    }
}
