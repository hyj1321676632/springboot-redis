package com.spring.redis.service;


import com.alibaba.fastjson.JSON;
import com.spring.redis.annotation.RedisCache;
import com.spring.redis.entity.UserInfo;
import com.spring.redis.enums.RedisOptType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void getRedisInfo(String key) {
        Properties properties = redisTemplate.getRequiredConnectionFactory().getConnection().info(key);
        log.info(JSON.toJSONString(properties));
    }

    public String addCache(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.opsForHash().put("ad-token", String.valueOf(System.currentTimeMillis()), "token1");
        redisTemplate.opsForHash().put("ad-token", String.valueOf(System.currentTimeMillis()), "token2");
        redisTemplate.opsForHash().put("ad-token", String.valueOf(System.currentTimeMillis()), "token3");
        return redisTemplate.opsForValue().get(key).toString();
    }


    @RedisCache
    public String getString(String key) {
        log.info("到数据库中查询数据......");
        return key;
    }

    @RedisCache
    public List<UserInfo> getList(List<UserInfo> list) {
        log.info("到数据库中查询数据......");
        return list;
    }

    @RedisCache(method = RedisOptType.GET, expireTime = 20L, timeUnit = TimeUnit.SECONDS)
    public Map<String, String> getMap(Map<String, String> map) {
        log.info("调用token生成接口获取token......");
        return map;
    }

    @RedisCache
    public Set<UserInfo> getSet(Set<UserInfo> set) {
        log.info("到数据库中查询数据......");
        return set;
    }

    @RedisCache
    public UserInfo getUserInfo(UserInfo userInfo) {
        log.info("到数据库中查询数据......");
        return userInfo;
    }

    public String testClass(String key) {
        return this.getString(key);
    }

    @RedisCache
    public String test1(Integer var1, Long var2) {
        log.info("test1......");
        return "1";
    }

    @RedisCache
    public String test1(Long var1, Integer var2) {
        log.info("test2......");
        return "1L";
    }
}
