package com.blue.zelda.code.starter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.assertj.core.api.Assertions.assertThat;

class CodeGeneratorAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CodeGeneratorAutoConfiguration.class))
            .withBean(RedisConnectionFactory.class, () -> org.mockito.Mockito.mock(RedisConnectionFactory.class))
            .withBean(StringRedisTemplate.class, () -> {
                StringRedisTemplate template = new StringRedisTemplate();
                template.setConnectionFactory(org.mockito.Mockito.mock(RedisConnectionFactory.class));
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(new StringRedisSerializer());
                return template;
            });

    @Test
    void testAutoConfiguration_enabledByDefault() {
        contextRunner
                .run(context -> {
                    assertThat(context).hasSingleBean(SmartCodeGenerator.class);
                    assertThat(context).hasSingleBean(CodeGeneratorProperties.class);
                });
    }

    @Test
    void testAutoConfiguration_disabledViaProperty() {
        contextRunner
                .withPropertyValues("zelda.code.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(SmartCodeGenerator.class);
                    assertThat(context).doesNotHaveBean(CodeGeneratorProperties.class);
                });
    }

    @Test
    void testAutoConfiguration_enabledViaProperty() {
        contextRunner
                .withPropertyValues("zelda.code.enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(SmartCodeGenerator.class);
                    assertThat(context).hasSingleBean(CodeGeneratorProperties.class);
                });
    }

    @Test
    void testCodeGeneratorProperties_defaultValues() {
        contextRunner
                .run(context -> {
                    CodeGeneratorProperties properties = context.getBean(CodeGeneratorProperties.class);
                    assertThat(properties.isEnabled()).isTrue();
                    assertThat(properties.getSeqLength()).isEqualTo(4);
                    assertThat(properties.getThreshold()).isEqualTo(9000);
                    assertThat(properties.getMaxNode()).isEqualTo(10);
                });
    }

    @Test
    void testCodeGeneratorProperties_customValues() {
        contextRunner
                .withPropertyValues(
                        "zelda.code.enabled=true",
                        "zelda.code.seqLength=6",
                        "zelda.code.threshold=5000",
                        "zelda.code.maxNode=20"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(SmartCodeGenerator.class);
                    assertThat(context).hasSingleBean(CodeGeneratorProperties.class);

                    CodeGeneratorProperties properties = context.getBean(CodeGeneratorProperties.class);
                    assertThat(properties.getSeqLength()).isEqualTo(6);
                    assertThat(properties.getThreshold()).isEqualTo(5000);
                    assertThat(properties.getMaxNode()).isEqualTo(20);
                });
    }

    @Test
    void testSmartCodeGenerator_beanIsSingleton() {
        contextRunner
                .run(context -> {
                    SmartCodeGenerator bean1 = context.getBean(SmartCodeGenerator.class);
                    SmartCodeGenerator bean2 = context.getBean(SmartCodeGenerator.class);
                    assertThat(bean1).isSameAs(bean2);
                });
    }

    @Test
    void testPropertiesBeanIsSingleton() {
        contextRunner
                .run(context -> {
                    CodeGeneratorProperties bean1 = context.getBean(CodeGeneratorProperties.class);
                    CodeGeneratorProperties bean2 = context.getBean(CodeGeneratorProperties.class);
                    assertThat(bean1).isSameAs(bean2);
                });
    }
}
