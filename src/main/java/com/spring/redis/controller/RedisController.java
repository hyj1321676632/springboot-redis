package com.spring.redis.controller;

//import com.spring.redis.entity.UserInfo;

import com.spring.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @RequestMapping(value = "/addCache", method = RequestMethod.GET)
    public Object addCache(@RequestParam String key) {
        return redisService.getString(key);
    }

}
