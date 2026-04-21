package com.blue.zelda.fw.config;

import com.blue.zelda.fw.trans.AutoTranslateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public AutoTranslateSerializer autoTranslateSerializer() {
        return new AutoTranslateSerializer();
    }
}
