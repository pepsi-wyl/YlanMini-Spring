package com.ylan.spring.aop;


// 提供调用切面方法的类工厂
public interface AspectInstanceFactory {
    Object getAspectInstance();
}
