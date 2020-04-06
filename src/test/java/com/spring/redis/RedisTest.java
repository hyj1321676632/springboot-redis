package com.spring.redis;

import com.alibaba.fastjson.JSON;
import com.spring.redis.entity.UserInfo;
import com.spring.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@Slf4j
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisService redisService;

    @Autowired
    private StringEncryptor stringEncryptor;

    // 解密
    @Test
    void test() {
        log.info(stringEncryptor.encrypt(""));
    }

    @Test
    void test1() {
        Long result = redisService.getString(12345678L);
        log.info(result.toString());
    }

    @Test
    void test2() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("张三");
        userInfo.setId(24);
        List<UserInfo> list = new ArrayList<>();
        list.add(userInfo);
        list = redisService.getList(list);
        log.info(JSON.toJSONString(list));

    }

    @Test
    void test3() {
        Map<String, String> map = new HashMap<>();
        map.put("token","x44sdf4hhhkkalsysefaj0");
        Map<String, String> result = redisService.getMap(map);
        log.info(JSON.toJSONString(result));
    }

    @Test
    void test4() {
        Set<UserInfo> set = new HashSet<>();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("张三三");
        userInfo.setId(25);
        set.add(userInfo);
        set = redisService.getSet(set);
        log.info(JSON.toJSONString(set));
    }

    @Test
    void test5(){
        redisService.getRedisInfo("Replication");
    }

    @Test
    void test6(){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("zhang123456");
        userInfo.setId(188);
        userInfo.setUserId("userId");
        userInfo.setCreateDate(null);
        userInfo.setUserPassword("**");
        userInfo.setUserRole("dev");
        userInfo.setUserSchool("hafu");
        userInfo.setUserSex("男");
        userInfo = redisService.getUserInfo(userInfo);
        log.info(JSON.toJSONString(userInfo));
    }

    @Test
    void test7(){
        redisService.test1(11L,11);
    }

//    @Test
//    void test8(){
//        String result = redisService.testClass("通过cglib生成代理对象，设置为true表示通过aop框架去代码该对象");
//        log.info(result);
//
//    }
}
