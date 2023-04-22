package com.ylan.spring.aop.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class AopUtils {
    public static Object invokeJoinpointUsingReflection(Object target, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}
