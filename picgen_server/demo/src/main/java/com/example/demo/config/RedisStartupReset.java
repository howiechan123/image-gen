package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import org.springframework.data.redis.core.StringRedisTemplate;

@Component
public class RedisStartupReset {

    private final StringRedisTemplate redisTemplate;

    public RedisStartupReset(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void clearRedisOnStartup() {
        try {
            redisTemplate.delete("hf_queue_count");
            redisTemplate.opsForValue().set("hf_queue_count", "0");
            System.out.println("Redis reset on startup: queue_count set to 0");
        } catch (Exception e) {
            System.err.println("Failed to clear Redis on startup: " + e.getMessage());
        }
    }
}

