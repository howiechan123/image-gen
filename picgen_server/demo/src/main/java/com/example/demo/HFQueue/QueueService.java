package com.example.demo.HFQueue;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private final StringRedisTemplate redisTemplate;
    private static final String QUEUE_KEY = "hf_queue_count";

    public QueueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public int incrementQueue() {
        return redisTemplate.opsForValue().increment(QUEUE_KEY).intValue();
    }

    public int decrementQueue() {
        return redisTemplate.opsForValue().decrement(QUEUE_KEY).intValue();
    }

    public int getQueueCount() {
        String count = redisTemplate.opsForValue().get(QUEUE_KEY);
        return count != null ? Integer.parseInt(count) : 0;
    }
}
