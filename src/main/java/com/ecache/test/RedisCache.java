package com.ecache.test;

import com.ecache.AbstractEasyCache;
import com.ecache.CacheConfig;
import com.ecache.annotation.DefaultCache;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author 谢俊权
 * @create 2016/8/2 10:53
 */
@DefaultCache
public class RedisCache extends AbstractEasyCache {
    private JedisPool jedisPool;

    public RedisCache(JedisPoolConfig config, String ip, int port, int timeout) {
        this(null, config, ip, port, timeout);
    }

    public RedisCache(CacheConfig cacheConfig, JedisPoolConfig jedisPoolConfig, String ip, int port, int timeout) {
        super(cacheConfig);
        this.jedisPool = new JedisPool(jedisPoolConfig, ip, port, timeout);
    }

    @Override
    public String setString(String key, String value, int expiredSeconds) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.setex(key, expiredSeconds, value);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return value;
    }

    @Override
    public String getString(String key) {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            return jedis.get(key);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return null;
    }
}
