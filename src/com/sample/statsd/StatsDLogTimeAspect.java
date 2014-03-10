package com.sample.statsd;

import org.apache.commons.lang.WordUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: jim
 * Date: 08/10/13
 * Time: 10:15
 */
@Aspect
public class StatsDLogTimeAspect {

    @Autowired
    private NonBlockingStatsDClient statsDClient;

    /*
     * Pointcut to match all the public methods.
    */
    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}


    @Around("publicMethod() && @annotation(LogExecutionTime)")
    public Object executionTimeRecorder(ProceedingJoinPoint pjp, LogExecutionTime LogExecutionTime) throws Throwable {

        Object o = null;


        // Spring injection might not be required/available on unit tests
        if (statsDClient != null) {

            long startTime = System.currentTimeMillis();
            o = pjp.proceed();
            long endTime = System.currentTimeMillis();

            /**
             * If annotation has provided source, use it. Otherwise use the declaring class name
             */
            String provider = LogExecutionTime.source() != null && !LogExecutionTime.source().isEmpty()
                    ? LogExecutionTime.source() :  pjp.getSignature().getDeclaringType().getSimpleName();

            String metricName = WordUtils.capitalize(provider) + "." + WordUtils.capitalize(pjp.getSignature().getName());

            statsDClient.recordExecutionTime(metricName, (int) (endTime - startTime));
            statsDClient.incrementCounter(metricName);
        }  else {

            o = pjp.proceed();
        }
        return o;
    }

    public void setStatsDClient(NonBlockingStatsDClient statsDClient) {
        this.statsDClient = statsDClient;
    }
}