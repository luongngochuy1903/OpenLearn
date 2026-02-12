package com.example.online.annotation.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingEveryServiceAspect {

    @Before("execution(* com.example.online.*.*(..)")
    public void loggingEveryClass(JoinPoint joinPoint){
        Class<?> clazz = joinPoint.getTarget().getClass();
        Logger LOG = LoggerFactory.getLogger(clazz);
        LOG.info("Method called: {}", joinPoint.getSignature().getName());
    }
}
