package cn.ldm.commons.utils.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckCache {
    String key() default "";

    String namespace() default "";

    int timeToLive() default 3600;

    int timeToIdle() default 0;

    String type() default "memcacheManager";

    boolean cacheNull() default true;

    boolean autoKeyPre() default true;
}
