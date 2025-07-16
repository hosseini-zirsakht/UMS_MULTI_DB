package com.example.UMS_MultiDB.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(com.example.UMS_MultiDB.controller..*)")
    public void controllerMethods() {}

    @Pointcut("within(com.example.UMS_MultiDB.service..*)")
    public void serviceMethods() {}

    @Pointcut("within(com.example.UMS_MultiDB.repository..*)")
    public void repositoryMethods() {}

    @Before("controllerMethods() || serviceMethods() || repositoryMethods()")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        logger.info("Method execution started: {}.{}() with arguments: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "controllerMethods() || serviceMethods() || repositoryMethods()",
            returning = "result")
    public void logAfterMethodSuccess(JoinPoint joinPoint, Object result) {
        logger.info("Method executed successfully: {}.{}() with result: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result != null ? result.toString() : "null");
    }

    @AfterThrowing(pointcut = "controllerMethods() || serviceMethods() || repositoryMethods()",
            throwing = "exception")
    public void logAfterMethodFailure(JoinPoint joinPoint, Exception exception) {
        logger.error("Method execution failed: {}.{}() with exception: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }
}