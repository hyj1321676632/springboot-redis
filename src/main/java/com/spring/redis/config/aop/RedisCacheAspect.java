package com.spring.redis.config.aop;

import com.alibaba.fastjson.JSON;
import com.spring.redis.annotation.RedisCache;
import com.spring.redis.enums.RedisOptType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
@Aspect
@Component
public class RedisCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private KeyGenerator keyGenerator;

    @Pointcut("@annotation(com.spring.redis.annotation.RedisCache)")
    public void pointcut() {
    }

    @Before(value = "pointcut()")
    public void before() {
    }

    @After(value = "pointcut()")
    public void after() {
    }

    @Around("pointcut() && @annotation(redisCache)")
    public Object around(ProceedingJoinPoint joinPoint, RedisCache redisCache) throws Throwable {
        String key = redisCache.key();
        RedisOptType redisMethod = redisCache.method();
        long expireTime = redisCache.expireTime();
        TimeUnit timeUnit = redisCache.timeUnit();
        // 获取调用方法的返回类型
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Class returnType = method.getReturnType();
        // 不为空说明是自定义的key
        if (StringUtils.isEmpty(key)) {
            if (!(joinPoint.getSignature() instanceof MethodSignature)) {
                throw new IllegalArgumentException("该注解只适用于方法");
            }
            key = keyGenerator.generate(joinPoint.getTarget(), method, joinPoint.getArgs()).toString();
        }
        Object object = null;
        if (RedisOptType.GET.equals(redisMethod)) {
            if (redisTemplate.hasKey(key)) {
                switch (redisTemplate.type(key)) {
                    case STRING:
                        object = redisTemplate.opsForValue().get(key);
                        if (object != null && !(object instanceof String))
                            return JSON.parseObject(object.toString(), returnType);
                        return object;
                    case HASH:
                        return redisTemplate.opsForHash().entries(key);
                    case LIST:
                        return redisTemplate.opsForList().range(key, 0, -1);
                    case SET:
                        return redisTemplate.opsForSet().members(key);
                    case ZSET:
                        return redisTemplate.opsForZSet().range(key, 0, -1);
                }
            }
            // 不存在则从数据库中取并存入缓存
            object = joinPoint.proceed();
            this.saveRedisCache(returnType, key, object, expireTime, timeUnit);
        } else if (RedisOptType.PUT.equals(redisMethod)) {
            object = joinPoint.proceed();
            this.saveRedisCache(returnType, key, object, expireTime, timeUnit);
        } else if (RedisOptType.DELETE.equals(redisMethod)) {
            redisTemplate.delete(key);
        }
        return object;
    }

    public void saveRedisCache(Class returnType, String key, Object value, long expireTime, TimeUnit timeUnit) {
        if (Map.class.equals(returnType)) {
            redisTemplate.opsForHash().putAll(key, value != null ? (Map) value : null);
        } else if (List.class.equals(returnType)) {
            redisTemplate.opsForList().rightPushAll(key, value != null ? (List) value : null);
        } else if (Set.class.equals(returnType)) {
            redisTemplate.opsForSet().add(key, value != null ? (Set) value : null);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
        if (expireTime != 0)
            redisTemplate.expire(key, expireTime, timeUnit);
    }
}
