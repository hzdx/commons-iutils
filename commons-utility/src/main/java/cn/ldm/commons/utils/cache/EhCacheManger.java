package cn.ldm.commons.utils.cache;

import org.springframework.beans.factory.annotation.Autowired;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class EhCacheManger implements CacheManager {

    @Autowired
    private Ehcache ehcache;

    public void put(String key, Object value, int timeToLive) {
        if ((key == null) || (value == null))
            return;
        this.ehcache.put(new Element(key, value, null, Integer.valueOf(0), Integer.valueOf(timeToLive)));
    }

    public boolean add(String key, Object value, int timeToLive) {
        if ((key == null) || (value == null))
            return false;
        return this.ehcache.putIfAbsent(new Element(key, value, null, Integer.valueOf(0), Integer.valueOf(timeToLive))) == null;
    }

    public Object get(String key) {
        if (key == null)
            return null;
        Element element = this.ehcache.get(key);
        return element != null ? element.getObjectValue() : null;
    }

    public Object getAndTouch(String key, int timeToIdle) {
        if (key == null)
            return null;
        Element element = this.ehcache.get(key);
        if (element != null) {
            if (element.getTimeToIdle() != timeToIdle) {
                element.setTimeToIdle(timeToIdle);
                this.ehcache.put(element);
            }
            return element.getObjectValue();
        }
        return null;
    }

    public void delete(String key) {
        if (key == null)
            return;
        this.ehcache.remove(key);
    }

    public boolean containsKey(String key) {
        if (key == null)
            return false;
        if (this.ehcache != null) {
            return this.ehcache.isKeyInCache(key);
        }
        return false;
    }

    public long incr(String key, long delta, int timeToLive) {
        if ((key == null) || (delta == 0L))
            return -1L;
        if (this.ehcache != null) {
            Element element = this.ehcache.putIfAbsent(new Element(key, new Long(delta), null, Integer.valueOf(0), Integer.valueOf(timeToLive)));
            if (element == null) {
                return delta;
            }
            long value = ((Long) element.getObjectValue()).longValue() + delta;
            this.ehcache.put(new Element(key, new Long(value), null, Integer.valueOf(0), Integer.valueOf(timeToLive)));
            return value;
        }

        return -1L;
    }
}
