package cn.ldm.commons.utils.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.rubyeye.xmemcached.MemcachedClient;

public class MemCacheManager implements CacheManager {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private MemcachedClient memcachedClient;

	public void put(String key, Object value, int timeToLive) {
		if ((key == null) || (value == null))
			return;
		if (this.memcachedClient == null) {
			this.log.debug("memcachedClient is null");
			return;
		}
		try {
			this.memcachedClient.set(key, timeToLive, value);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
	}

	public boolean add(String key, Object value, int timeToLive) {
		if ((key == null) || (value == null))
			return false;
		if (this.memcachedClient == null) {
			this.log.debug("memcachedClient is null");
			return false;
		}
		try {
			return this.memcachedClient.add(key, timeToLive, value);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return false;
	}

	public Object getAndTouch(String key, int timeToLive) {
		if (key == null)
			return null;
		if (this.memcachedClient == null) {
			this.log.debug("memcachedClient is null");
			return null;
		}
		try {
			return this.memcachedClient.getAndTouch(key, timeToLive);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return null;
	}

	public Object get(String key) {
		if (key == null)
			return null;
		if (this.memcachedClient == null) {
			this.log.debug("memcachedClient is null");
			return null;
		}
		try {
			return this.memcachedClient.get(key);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return null;
	}

	public void delete(String key) {
		if (key == null)
			return;
		if (this.memcachedClient == null) {
			this.log.debug("memcachedClient is null");
			return;
		}
		try {
			this.memcachedClient.delete(key);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
	}

	public boolean containsKey(String key) {
		if (key == null)
			return false;
		if (this.memcachedClient == null) {
			this.log.debug("memcachedClient is null");
			return false;
		}
		try {
			return this.memcachedClient.get(key) != null;
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return false;
	}

	public long incr(String key, long delta, int timeToLive) {
		if (key == null)
			return -1L;
		if (this.memcachedClient == null) {
			this.log.debug("memcachedClient is null");
			return -1L;
		}
		try {
			return this.memcachedClient.incr(key, delta, delta, 2000L, timeToLive);
		} catch (Exception e) {
			this.log.error(e.getMessage(), e);
		}
		return -1L;
	}
}
