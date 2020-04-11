package com.spring.redis.annotation;

import com.spring.redis.enums.RedisOptType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {

    String key() default "";

    RedisOptType method() default RedisOptType.GET;

    long expireTime() default 5L;

    TimeUnit timeUnit() default TimeUnit.MINUTES;

}
