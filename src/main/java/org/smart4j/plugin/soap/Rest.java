package org.smart4j.plugin.soap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * REST服务注解
 * Created by Administrator on 2017/5/4 0004.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Rest {
    /**
     * 服务名
     */
    String value() default "";
}
