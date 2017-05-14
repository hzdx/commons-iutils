package cn.ldm.commons.utils.cache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EvictCache {
	public abstract String key();

	public abstract String namespace();

	public abstract String type();
}
