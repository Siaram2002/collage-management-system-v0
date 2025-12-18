//package com.cms.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
///**
// * CommonAspect provides a reusable AOP layer for all cross-cutting concerns.
// *
// * You can add multiple advices here like:
// *  - Logging
// *  - Performance measurement
// *  - Exception handling
// *  - Security checks
// *
// * Currently it covers all service layer methods.
// */
//@Aspect
//@Component
//public class CommonAspect {
//
//    private static final Logger logger = LoggerFactory.getLogger(CommonAspect.class);
//
//    /**
//     * Pointcut for all methods inside the service layer.
//     * Adjust package according to your project structure.
//     */
//    @Pointcut("execution(* com.cms.service..*(..))")
//    public void allServiceMethods() {}
//
//    /**
//     * Around advice: executes before and after method.
//     * Can be used for logging, timing, or other cross-cutting concerns.
//     */
//    @Around("allServiceMethods()")
//    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
//        String methodName = joinPoint.getSignature().getName();
//        logger.info("Starting method: {}", methodName);
//
//        long startTime = System.currentTimeMillis();
//        Object result;
//        try {
//            // Proceed with the method execution
//            result = joinPoint.proceed();
//        } catch (Exception ex) {
//            // Cross-cutting exception handling
//            logger.error("Exception in method {}: {}", methodName, ex.getMessage(), ex);
//            throw ex; // rethrow after handling
//        }
//
//        long endTime = System.currentTimeMillis();
//        logger.info("Finished method: {} | Execution time: {} ms", methodName, endTime - startTime);
//
//        return result;
//    }
//
//    /**
//     * Optional: Before advice.
//     * Executes before any method in the service layer.
//     */
//    @Before("allServiceMethods()")
//    public void beforeMethod() {
//        logger.debug("Executing @Before advice for service method.");
//    }
//
//    /**
//     * Optional: After advice.
//     * Executes after any method in the service layer, regardless of outcome.
//     */
//    @After("allServiceMethods()")
//    public void afterMethod() {
//        logger.debug("Executing @After advice for service method.");
//    }
//
//    /**
//     * Optional: After returning advice.
//     * Executes only when a method returns successfully.
//     */
//    @AfterReturning(pointcut = "allServiceMethods()", returning = "result")
//    public void afterReturning(Object result) {
//        logger.debug("Method returned successfully: {}", result);
//    }
//
//    /**
//     * Optional: After throwing advice.
//     * Executes only when a method throws an exception.
//     */
//    @AfterThrowing(pointcut = "allServiceMethods()", throwing = "ex")
//    public void afterThrowing(Exception ex) {
//        logger.warn("Method threw exception: {}", ex.getMessage(), ex);
//    }
//}
