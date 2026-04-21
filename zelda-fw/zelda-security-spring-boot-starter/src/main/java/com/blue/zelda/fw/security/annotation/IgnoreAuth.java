package com.blue.zelda.fw.security.annotation;


import java.lang.annotation.*;

/**
 * 忽略认证注解
 *
 * <p>标记在类或方法上，表示该类或方法不需要进行登录认证即可访问。
 * 可用于公开接口、健康检查、静态资源等场景。</p>
 *
 * <p>使用示例：</p>
 * <pre>
 * &#64;IgnoreAuth
 * &#64;GetMapping("/public")
 * public Result publicApi() { ... }
 * </pre>
 *
 * @author zelda
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IgnoreAuth {
}
