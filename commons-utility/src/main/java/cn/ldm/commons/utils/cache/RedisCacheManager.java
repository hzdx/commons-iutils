package cn.ldm.commons.utils.cache;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisCacheManager implements CacheManager {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void put(String key, Object value, int timeToLive) {
        if ((key == null) || (value == null))
            return;
        if (this.redisTemplate == null) {
            this.log.debug("redisTemplate is null");
            return;
        }
        try {
            if (timeToLive > 0)
                this.redisTemplate.opsForValue().set(key, value, timeToLive);
            else
                this.redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            this.log.error(e.getMessage(), e);
        }
    }

    public boolean add(String key, Object value, int timeToLive) {
        if ((key == null) || (value == null))
            return false;
        if (this.redisTemplate == null) {
            this.log.debug("redisTemplate is null");
            return false;
        }
        try {
            boolean success = this.redisTemplate.opsForValue().setIfAbsent(key, value).booleanValue();
            if ((success) && (timeToLive > 0)) {
                this.redisTemplate.expire(key, timeToLive, TimeUnit.SECONDS);
            }
            return success;
        } catch (Exception e) {
            this.log.error(e.getMessage(), e);
        }
        return false;
    }

    public Object getAndTouch(String key, int timeToLive) {
        if (key == null)
            return null;
        if (this.redisTemplate == null) {
            this.log.debug("redisTemplate is null");
            return null;
        }
        Object rs = this.redisTemplate.opsForValue().get(key);
        if ((rs != null) && (timeToLive > 0)) {
            this.redisTemplate.expire(key, timeToLive, TimeUnit.SECONDS);
        }
        return rs;
    }

    public Object get(String key) {
        if (key == null)
            return null;
        if (this.redisTemplate == null) {
            this.log.debug("redisTemplate is null");
            return null;
        }
        try {
            return this.redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            this.log.error(e.getMessage(), e);
        }
        return null;
    }

    public void delete(String key) {
        if (key == null)
            return;
        if (this.redisTemplate == null) {
            this.log.debug("redisTemplate is null");
            return;
        }
        try {
            this.redisTemplate.delete(key);
        } catch (Exception e) {
            this.log.error(e.getMessage(), e);
        }
    }

    public boolean containsKey(String key) {
        if (key == null)
            return false;
        if (this.redisTemplate == null) {
            this.log.debug("redisTemplate is null");
            return false;
        }
        try {
            return this.redisTemplate.hasKey(key).booleanValue();
        } catch (Exception e) {
            this.log.error(e.getMessage(), e);
        }
        return false;
    }

    public long incr(String key, long delta, int timeToLive) {
        if (key == null)
            return -1L;
        if (this.redisTemplate == null) {
            this.log.debug("redisTemplate is null");
            return -1L;
        }
        try {
            long result = this.redisTemplate.opsForValue().increment(key, delta).longValue();
            if (timeToLive > 0)
                this.redisTemplate.expire(key, timeToLive, TimeUnit.SECONDS);
            return result;
        } catch (Exception e) {
            this.log.error(e.getMessage(), e);
        }
        return -1L;
    }
}

