package com.spring.redis.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    @NotNull
    private String nodes;
    @NotNull
    private String password;
    @NotNull
    private JedisPoolProperties jedisPool;

}
