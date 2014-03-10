package com.sample.statsd;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: jim
 * Date: 08/10/13
 * Time: 10:47
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited // Only valid for classes - does not apply to interfaces
public @interface LogExecutionTime {
    String source() default "";
}