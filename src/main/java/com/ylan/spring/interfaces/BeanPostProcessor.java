package com.ylan.spring.interfaces;

/**
 * 在 bean 初始化前后对其进行一些操作，使程序员可以干涉 bean 初始化的过程
 * 可以对 bean 进行功能扩展、增强
 * AOP 就是在这里进行
 * AOP 返回一个代理对象
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 20:01
 */

public interface BeanPostProcessor {
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}