package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisStartupReset {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisStartupReset(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void clearRedisOnStartup() {
        try {
            redisTemplate.delete("queue_count");

            // full reset
            // redisTemplate.getConnectionFactory().getConnection().flushAll();

            System.out.println("Redis reset on startup: queue_count cleared");
        } catch (Exception e) {
            System.err.println("Failed to clear Redis on startup: " + e.getMessage());
        }
    }
}
