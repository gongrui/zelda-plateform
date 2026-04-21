package com.blue.zelda.fw.trans;

import com.blue.zelda.fw.trans.annon.Dict;
import com.blue.zelda.fw.trans.annon.Ref;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;


public class AutoTranslateSerializer extends StdSerializer<Object>
        implements ContextualSerializer, ApplicationContextAware {

    // 全局获取 Spring Bean（最稳定、SB4 通用）
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    private final Dict dict;
    private final Ref ref;

    // 无参构造（Jackson 必须）
    public AutoTranslateSerializer() {
        super(Object.class);
        this.dict = null;
        this.ref = null;
    }

    // 带注解构造
    public AutoTranslateSerializer(Dict dict, Ref ref) {
        super(Object.class);
        this.dict = dict;
        this.ref = ref;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeObject(value);
        if (value == null) return;

        // 🔥 从静态上下文获取（绝对不报错！）
        TranslateCacheService cacheService = context.getBean(TranslateCacheService.class);

        String id = value.toString();
        String fieldName = gen.getOutputContext().getCurrentName();

        if (dict != null) {
            String name = cacheService.getDict(dict.dictCode(), id);
            gen.writeStringField(fieldName + "Name", name);
        }

        if (ref != null) {
            String name = switch (ref.type()) {
                case USER -> cacheService.getUserName(id);
                case ORG -> cacheService.getOrgName(id);
                case ROLE -> cacheService.getRoleName(id);
                default -> id;
            };
            gen.writeStringField(fieldName + "Name", name);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        Dict dict = property.getAnnotation(Dict.class);
        Ref ref = property.getAnnotation(Ref.class);
        return new AutoTranslateSerializer(dict, ref);
    }
}