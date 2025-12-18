//package com.cms.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
///**
// * ControllerAspect handles cross-cutting concerns for all controller methods.
// *
// * You can add logging, execution time tracking, exception handling, etc.
// */
//@Aspect
//@Component
//public class ControllerAspect {
//
//    private static final Logger logger = LoggerFactory.getLogger(ControllerAspect.class);
//
//    /**
//     * Pointcut to match all methods in classes annotated with @RestController or @Controller.
//     */
//    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || " +
//            "within(@org.springframework.stereotype.Controller *)")
//    public void controllerMethods() {}
//
//    /**
//     * Around advice to log execution time and method details.
//     */
//    @Around("controllerMethods()")
//    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
//        String methodName = joinPoint.getSignature().toShortString();
//        logger.info("Controller method start: {}", methodName);
//
//        long startTime = System.currentTimeMillis();
//        Object result;
//        try {
//            result = joinPoint.proceed(); // Execute the controller method
//        } catch (Exception ex) {
//            logger.error("Exception in controller method {}: {}", methodName, ex.getMessage(), ex);
//            throw ex; // rethrow for normal Spring exception handling
//        }
//        long endTime = System.currentTimeMillis();
//
//        logger.info("Controller method finished: {} | Execution time: {} ms", methodName, endTime - startTime);
//        return result;
//    }
//
//    /**
//     * Optional: Before advice for controller methods.
//     */
//    @Before("controllerMethods()")
//    public void beforeController() {
//        logger.debug("Before executing controller method");
//    }
//
//    /**
//     * Optional: After advice for controller methods.
//     */
//    @After("controllerMethods()")
//    public void afterController() {
//        logger.debug("After executing controller method");
//    }
//
//    /**
//     * Optional: After returning advice.
//     */
//    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
//    public void afterReturningController(Object result) {
//        logger.debug("Controller returned successfully: {}", result);
//    }
//
//    /**
//     * Optional: After throwing advice.
//     */
//    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
//    public void afterThrowingController(Exception ex) {
//        logger.warn("Controller method threw exception: {}", ex.getMessage(), ex);
//    }
//}
