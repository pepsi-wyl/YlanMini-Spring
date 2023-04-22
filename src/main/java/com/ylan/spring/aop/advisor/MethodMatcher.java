package com.ylan.spring.aop.advisor;

import java.lang.reflect.Method;

/**
 * Part of a Pointcut: Checks whether the target method is eligible for advice.
 * @author by pepsi-wyl
 * @date 2023-04-20 21:15
 */
public interface MethodMatcher {

    /**
     * Perform static checking whether the given method matches.
     *
     * @param method      the candidate method
     * @param targetClass the target class
     * @return whether or not this method matches statically
     */
    boolean matches(Method method, Class<?> targetClass);

}
