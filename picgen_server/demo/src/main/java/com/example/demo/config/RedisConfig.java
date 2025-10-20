package com.example.demo.config;


import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        String redisUrl = System.getenv("REDIS_URL");
        if (redisUrl == null || redisUrl.isEmpty()) {
            throw new IllegalStateException("REDIS_URL environment variable is missing");
        }

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        URI uri = URI.create(redisUrl);

        config.setHostName(uri.getHost());
        config.setPort(uri.getPort());

        if (uri.getUserInfo() != null && uri.getUserInfo().contains(":")) {
            String[] userInfo = uri.getUserInfo().split(":", 2);
            config.setUsername(userInfo[0]);
            config.setPassword(userInfo[1]);
        }

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
