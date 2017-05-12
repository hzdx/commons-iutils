package cn.ldm.commons.utils.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

//在spring容器中注入这个类
//以使得容器外的对象 通过调用这个类的静态方法 getBean()获取spring容器内的对象
public class SpringContextHolder implements ApplicationContextAware {

	private static ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		SpringContextHolder.ctx = ctx;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		return (T) ctx.getBean(beanName);
	}

	public static <T> T getBean(Class<T> clazz) {
		return ctx.getBean(clazz);
	}

}
