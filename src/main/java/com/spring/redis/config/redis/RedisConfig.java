package com.spring.redis.config.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.spring.redis.config.property.JedisPoolProperties;
import com.spring.redis.config.property.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
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
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@EnableCaching
@Configuration
public class RedisConfig  {

    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        log.info("redis nodes : {}  jedispool : {} ", redisProperties.getNodes(), redisProperties.getJedisPool());
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
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        // 建议使用这种方式，小范围指定白名单
        ParserConfig.getGlobalInstance().addAccept("com.spring.redis.entity");
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(fastJsonRedisSerializer);
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Duration ttl = Duration.ofSeconds(20L);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(ttl);
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory)).cacheDefaults(redisCacheConfiguration).build();
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
