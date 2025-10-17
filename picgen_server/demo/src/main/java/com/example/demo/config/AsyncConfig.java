package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ExecutorService delegate = Executors.newCachedThreadPool();

        // Wrap executor so SecurityContext is propagated
        DelegatingSecurityContextExecutor securityExecutor = new DelegatingSecurityContextExecutor(delegate);

        // Wrap again to propagate RequestAttributes
        return runnable -> {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            securityExecutor.execute(() -> {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                try {
                    runnable.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            });
        };
    }

    // Optional helper for Callable tasks
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        return () -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            try {
                return callable.call();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }
}
