package com.spring.redis.config;

import com.spring.redis.config.property.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@EnableConfigurationProperties(RedisProperties.class)
@EnableAspectJAutoProxy(exposeProxy = true)
@ComponentScan({"com.spring.redis.config"})
@Configuration
public class AppConfig {
}
