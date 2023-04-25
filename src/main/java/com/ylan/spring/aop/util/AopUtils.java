package com.ylan.spring.aop.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// AOP工具类
public class AopUtils {
    // 触发目标类方法 return method.invoke(target, args); 触发目标类方法
    public static Object invokeJoinpointUsingReflection(Object target, Method method, Object[] args) throws Throwable, IllegalAccessException {
        try {
            // 执行切入点方法
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
