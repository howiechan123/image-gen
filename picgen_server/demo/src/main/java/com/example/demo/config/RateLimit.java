package com.example.demo.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;


@Retention(RetentionPolicy.RUNTIME) // available at runtime
@Target(ElementType.METHOD)         // attach to controller methods
public @interface RateLimit {
    int limit() default 10;         // max requests
    int period() default 60;        // time window in seconds
}
