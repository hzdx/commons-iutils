package cn.ldm.commons.utils.cache;

public abstract interface CacheManager {
    public static final String EHCACHE = "ehcacheManager";
    public static final String MEMCACHE = "memcacheManager";
    public static final String REDIS = "rediscacheManager";
    public static final String DEFAULT = "memcacheManager";

    public abstract void put(String paramString, Object paramObject, int paramInt);

    public abstract boolean add(String paramString, Object paramObject, int paramInt);

    public abstract Object getAndTouch(String paramString, int paramInt);

    public abstract Object get(String paramString);

    public abstract void delete(String paramString);

    public abstract boolean containsKey(String paramString);

    public abstract long incr(String paramString, long paramLong, int paramInt);
}
