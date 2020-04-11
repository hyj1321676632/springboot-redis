package com.spring.redis.config.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.spring.redis.config.property.JedisPoolProperties;
import com.spring.redis.config.property.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class RedisConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // jedispool连接池配置
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPoolProperties jedisPoolProperties = redisProperties.getJedisPool();
        poolConfig.setMaxTotal(jedisPoolProperties.getMaxTotal());
        poolConfig.setMaxIdle(jedisPoolProperties.getMaxIdle());
        poolConfig.setNumTestsPerEvictionRun(jedisPoolProperties.getNumTestsPerEvictionRun());
        poolConfig.setTimeBetweenEvictionRunsMillis(jedisPoolProperties.getTimeBetweenEvictionRunsMillis());
        poolConfig.setMinEvictableIdleTimeMillis(jedisPoolProperties.getMinEvictableIdleTimeMillis());
        poolConfig.setSoftMinEvictableIdleTimeMillis(jedisPoolProperties.getSoftMinEvictableIdleTimeMillis());
        poolConfig.setMaxWaitMillis(jedisPoolProperties.getMaxWaitMillis());
        poolConfig.setTestOnBorrow(jedisPoolProperties.getTestOnBorrow());
        poolConfig.setTestWhileIdle(jedisPoolProperties.getTestWhileIdle());
        poolConfig.setBlockWhenExhausted(jedisPoolProperties.getBlockWhenExhausted());

        List<String> nodes = Arrays.asList(redisProperties.getNodes().split(","));
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(new RedisClusterConfiguration(nodes), poolConfig);
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        // 设置默认过期时间是30秒
        defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
        // 初始化RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new IgnoreCacheErrorHandler();
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder stringBuilder = new StringBuilder(target.getClass().getName());
            stringBuilder.append("#").append(method.getName());
            if (params != null) {
                stringBuilder.append(":");
                for (Object param : params) {
                    stringBuilder.append(":").append(param != null ? JSON.toJSONString(param) : "");
                }
            }
            return stringBuilder;
        };
    }
}
