package com.spring.redis.config.property;

import lombok.Data;

@Data
public class JedisPoolProperties {

    private Integer maxTotal;
    private Integer maxIdle;
    private Integer numTestsPerEvictionRun;
    private Integer timeBetweenEvictionRunsMillis;
    private Long minEvictableIdleTimeMillis;
    private Integer softMinEvictableIdleTimeMillis;
    private Integer maxWaitMillis;
    private Boolean testOnBorrow;
    private Boolean testWhileIdle;
    private Boolean blockWhenExhausted;

}
