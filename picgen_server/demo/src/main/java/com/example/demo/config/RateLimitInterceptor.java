package com.example.demo.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createBucket(int limit, int periodSeconds) {
        Refill refill = Refill.greedy(limit, Duration.ofSeconds(periodSeconds));
        Bandwidth bandwidth = Bandwidth.classic(limit, refill);
        return Bucket.builder().addLimit(bandwidth).build();
    }

    private Bucket resolveBucket(String key, int limit, int period) {
        return cache.computeIfAbsent(key, k -> createBucket(limit, period));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        int limit = 10;
        int period = 60; //seconds

        if (handler instanceof HandlerMethod handlerMethod) {
            RateLimit annotation = handlerMethod.getMethod().getAnnotation(RateLimit.class);
            if (annotation != null) {
                limit = annotation.limit();
                period = annotation.period();
            }
        }

        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        String key = ip + ":" + path;

        Bucket bucket = resolveBucket(key, limit, period);

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return false;
        }
    }
}
