package cn.ldm.commons.utils.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

public class BaseAspect implements Ordered {
    protected Logger log = LoggerFactory.getLogger(getClass());
    protected int order;

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    protected Object getResult(ProceedingJoinPoint jp)
            throws Throwable {
        Object[] args = jp.getArgs();
        Object result;
        if ((args == null) || (args.length == 0))
            result = jp.proceed();
        else {
            result = jp.proceed(args);
        }
        return result;
    }
}

