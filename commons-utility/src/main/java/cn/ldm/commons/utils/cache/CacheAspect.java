
package cn.ldm.commons.utils.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import cn.ldm.commons.utils.etc.GlobalConfig;
import cn.ldm.commons.utils.web.SpringContextHolder;

@Aspect
public class CacheAspect extends BaseAspect {
	private boolean warnNoDebugSymbolInformation;
	public ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

	public CacheAspect() {
	}

	@Around("execution(public * *(..)) && @annotation(checkCache)")
	public Object get(ProceedingJoinPoint jp, CheckCache checkCache) throws Throwable {
		String namespace = checkCache.namespace();
		if (namespace == null || namespace.equals("")) {
			// namespace = GlobalConfig.initProjectCode();
		}

		String fullkey;
		fullkey = this.getKey(jp, checkCache.key(), (Object) null, checkCache.autoKeyPre());
		fullkey = CacheUtil.getCacheKey(namespace, fullkey);
		CacheManager cache = (CacheManager) SpringContextHolder.getBean(checkCache.type());
		Object rs = null;

		try {
			if (checkCache.timeToIdle() > 0) {
				rs = cache.getAndTouch(fullkey, checkCache.timeToIdle());
			} else {
				rs = cache.get(fullkey);
			}
		} catch (Exception var9) {
			this.log.error("缓存获取失败[" + var9.getMessage() + "]");
		}

		if (rs == null || !checkCache.cacheNull() && rs instanceof EmptyCacheObject) {
			rs = this.getResult(jp);
			if (rs == null && checkCache.cacheNull()) {
				rs = new EmptyCacheObject();
			}

			if (rs != null) {
				try {
					cache.put(fullkey, rs,
							checkCache.timeToIdle() > 0 ? checkCache.timeToIdle() : checkCache.timeToLive());
				} catch (Exception var8) {
					this.log.error("缓存保存失败[" + var8.getMessage() + "]");
				}
			}
		}

		return rs != null && !(rs instanceof EmptyCacheObject) ? rs : null;
	}

	@AfterReturning(pointcut = "@annotation(evictCache)", returning = "retval")
	public void remove(JoinPoint jp, EvictCache evictCache, Object retval) {
		String namespace = evictCache.namespace();
		if (namespace == null || namespace.equals("")) {
			namespace = GlobalConfig.getProjectCode();
		}

		String keystr = evictCache.key();
		if (keystr == null || keystr.isEmpty()) {
			this.log.error("EvictCache Key不能为空");
		}

		String[] keys = keystr.split(",");
		String fullkey = null;
		String[] var11 = keys;
		int var10 = keys.length;

		for (int var9 = 0; var9 < var10; ++var9) {
			String key = var11[var9];
			fullkey = this.getKey(jp, key, retval, false);
			fullkey = CacheUtil.getCacheKey(namespace, fullkey);

			try {
				CacheManager e = (CacheManager) SpringContextHolder.getBean(evictCache.type());
				e.delete(fullkey);
			} catch (Exception var13) {
				this.log.error(var13.toString());
			}
		}

	}

	private String getKey(JoinPoint jp, String annKey, Object retval, boolean autoKeyPre) {
		String finalKey;
		if (annKey != null && !annKey.equals("")) {
			Map var14 = this.buildContext(jp);
			StandardEvaluationContext var15 = new StandardEvaluationContext();
			Iterator var17 = var14.keySet().iterator();

			while (var17.hasNext()) {
				String var16 = (String) var17.next();
				var15.setVariable(var16, var14.get(var16));
			}

			if (retval != null) {
				var15.setVariable("retval", retval);
			}

			SpelExpressionParser var18 = new SpelExpressionParser();
			String var19 = var18.parseExpression(annKey).getValue(var15).toString();
			if (autoKeyPre) {
				finalKey = this.getAutoKeyPre(jp.getSignature()) + "_" + var19;
			} else {
				finalKey = var19;
			}
		} else {
			String context = this.getAutoKeyPre(jp.getSignature());
			Object[] evalContext = jp.getArgs();
			if (evalContext != null && evalContext.length > 0) {
				boolean parser = true;
				StringBuffer newKey = new StringBuffer();
				Object[] var13 = evalContext;
				int var12 = evalContext.length;

				for (int var11 = 0; var11 < var12; ++var11) {
					Object o = var13[var11];
					if (o != null && !o.getClass().isPrimitive() && !(o instanceof String) && !(o instanceof Number)
							&& !(o instanceof Boolean) && !(o instanceof Character)) {
						parser = false;
						break;
					}

					newKey.append(o);
				}

				if (parser) {
					finalKey = context + "_" + newKey;
				} else {
					finalKey = context + "_" + Arrays.hashCode(evalContext);
				}
			} else {
				finalKey = context;
			}
		}

		return finalKey;
	}

	private String getAutoKeyPre(Signature sig) {
		String className = sig.getDeclaringTypeName();
		int index = className.lastIndexOf(".");
		if (index > 0) {
			className = className.substring(index + 1);
		}

		return className + "." + sig.getName();
	}

	public Map<String, Object> buildContext(JoinPoint jp) {
		HashMap context = new HashMap();
		Object[] args = jp.getArgs();
		String[] paramNames = this.getParameterNames(jp);
		if (paramNames == null) {
			if (!this.warnNoDebugSymbolInformation) {
				this.warnNoDebugSymbolInformation = true;
				System.out.println("Unable to resolve method parameter names for method: "
						+ jp.getStaticPart().getSignature()
						+ ". Debug symbol information is required if you are using parameter names in expressions.");
			}
		} else {
			for (int i = 0; i < args.length; ++i) {
				context.put(paramNames[i], args[i]);
			}
		}

		if (!context.containsKey("_this")) {
			context.put("_this", jp.getThis());
		}

		if (!context.containsKey("target")) {
			context.put("target", jp.getTarget());
		}

		return context;
	}

	public String[] getParameterNames(JoinPoint jp) {
		if (!jp.getKind().equals("method-execution")) {
			return null;
		} else {
			Class clz = jp.getTarget().getClass();
			MethodSignature sig = (MethodSignature) jp.getSignature();

			try {
				Method method = clz.getDeclaredMethod(sig.getName(), sig.getParameterTypes());
				if (method.isBridge()) {
					method = BridgeMethodResolver.findBridgedMethod(method);
				}

				return this.getParameterNames(method, (Constructor) null);
			} catch (Exception var6) {
				return null;
			}
		}
	}

	private String[] getParameterNames(Method method, Constructor<?> ctor) {
		Annotation[][] annotations = method != null ? method.getParameterAnnotations() : ctor.getParameterAnnotations();
		String[] names = new String[annotations.length];
		boolean allbind = true;

		for (int namesDiscovered = 0; namesDiscovered < annotations.length; ++namesDiscovered) {
			allbind = false;
		}

		if (!allbind) {
			String[] var8 = method != null ? this.parameterNameDiscoverer.getParameterNames(method)
					: this.parameterNameDiscoverer.getParameterNames(ctor);
			if (var8 == null) {
				return null;
			}

			for (int i = 0; i < names.length; ++i) {
				if (names[i] == null) {
					names[i] = var8[i];
				}
			}
		}

		return names;
	}
}
