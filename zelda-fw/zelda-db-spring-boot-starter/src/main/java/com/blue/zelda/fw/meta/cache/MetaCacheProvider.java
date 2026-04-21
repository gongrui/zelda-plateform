package com.blue.zelda.fw.meta.cache;

import com.blue.zelda.fw.meta.entity.SysMetadataDatasource;
import com.blue.zelda.fw.meta.entity.SysMetadataView;
import com.blue.zelda.fw.meta.mapper.MetadataDatasourceMapper;
import com.blue.zelda.fw.meta.mapper.MetadataViewMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 元数据缓存提供者
 *
 * <p>提供元数据（视图配置、数据源配置）的缓存访问能力。
 * 通过 {@link Cacheable} 注解实现缓存，Spring AOP 自动生效。</p>
 *
 * <p>缓存策略：</p>
 * <ul>
 *   <li>视图配置：cacheName = "meta_view"，key = viewCode</li>
 *   <li>数据源配置：cacheName = "meta_ds"，key = dsCode</li>
 * </ul>
 *
 * @author zelda
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class MetaCacheProvider {

    private final MetadataViewMapper viewMapper;
    private final MetadataDatasourceMapper datasourceMapper;

    /**
     * 获取视图配置（带缓存）
     *
     * @param viewCode 视图编码
     * @return 视图实体，如果不存在则返回 null
     */
    @Cacheable(value = "meta_view", key = "#viewCode")
    public SysMetadataView getView(String viewCode) {
        return viewMapper.selectOneByQuery(QueryWrapper.create()
                .eq("view_code", viewCode)
                .eq("is_enabled", true)
                .isNull("deleted_at"));
    }

    /**
     * 获取数据源配置（带缓存）
     *
     * @param dsCode 数据源编码
     * @return 数据源实体，如果不存在则返回 null
     */
    @Cacheable(value = "meta_ds", key = "#dsCode")
    public SysMetadataDatasource getDs(String dsCode) {
        return datasourceMapper.selectOneByQuery(QueryWrapper.create()
                .eq("data_code", dsCode)
                .eq("is_enabled", true)
                .isNull("deleted_at"));
    }
}
