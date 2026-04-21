package com.blue.zelda.fw.trans.annon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ref {
    Type type();
    // 关联的ID字段名（例如：updateBy 对应 updateByName）
    String idField() default "";

    enum Type {
        USER, ORG, ROLE
    }

}