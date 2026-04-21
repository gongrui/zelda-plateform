package com.blue.zelda.fw.config;

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MyBatisFlexConfig {

    public MyBatisFlexConfig() {
        // 全局配置
        FlexGlobalConfig defaultConfig = FlexGlobalConfig.getDefaultConfig();
        defaultConfig.setPrintBanner(false);  // 关闭Banner

        // 配置全局主键生成器为雪花ID生成器
        FlexGlobalConfig.KeyConfig keyConfig = new FlexGlobalConfig.KeyConfig();
        keyConfig.setKeyType(KeyType.Generator);
        keyConfig.setValue(KeyGenerators.snowFlakeId);
        defaultConfig.setKeyConfig(keyConfig);

        // 开启审计日志
        AuditManager.setAuditEnable(true);
        AuditManager.setMessageCollector(auditMessage ->
                log.info("SQL: {}, 耗时: {}ms", auditMessage.getFullSql(), auditMessage.getElapsedTime())
        );
    }
}
