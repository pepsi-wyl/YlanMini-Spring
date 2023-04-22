package com.ylan.spring.aop;

/**
 * 提供调用切面方法的类
 */
public interface AspectInstanceFactory {
    /**
     * Create an instance of this factory's aspect.
     * @return the aspect instance (never {@code null})
     */
    Object getAspectInstance();
}
