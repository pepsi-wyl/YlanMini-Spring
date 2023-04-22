package com.ylan.test;

import com.ylan.spring.anno.Component;
import com.ylan.spring.interfaces.BeanPostProcessor;

/**
 * @author by pepsi-wyl
 * @date 2023-04-20 20:03
 */

//@Component
public class TestBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("[[[[[[   TestMSG   BeanPostProcessor-postProcessBeforeInitialization-初始化前: " + beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("[[[[[[   TestMSG   BeanPostProcessor-postProcessAfterInitialization-初始化后: " + beanName);
        return bean;
    }
}
