package com.ylan.spring;

import com.ylan.spring.interfaces.DisposableBean;

/**
 * 适配器，适配以执行各种形式的销毁方法
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 20:24
 */

public class DisposableBeanAdapter implements DisposableBean {

    // 属性
    private Object bean;
    private String beanName;
    private BeanDefinition beanDefinition;

    // 构造方法
    public DisposableBeanAdapter(Object bean, String beanName, BeanDefinition beanDefinition) {
        this.bean = bean;
        this.beanName = beanName;
        this.beanDefinition = beanDefinition;
    }

    // 对象有销毁方法
    public static boolean hasDestroyMethod(Object bean, BeanDefinition beanDefinition) {
        // 对象 实现 DisposableBean 或者 AutoCloseable 接口
        if (bean instanceof DisposableBean || bean instanceof AutoCloseable) {
            return true;
        }
        return false;
    }

    // 对象销毁方法
    @Override
    public void destroy() {
        try {
            // 实现 DisposableBean 接口
            if (bean instanceof DisposableBean) {
                // 调用用户自定义的销毁方法逻辑
                ((DisposableBean) bean).destroy();
            }
            // 实现 AutoCloseable 接口
            else if (bean instanceof AutoCloseable) {
                // 调用用户自定义的销毁方法逻辑
                ((AutoCloseable) bean).close();
            }
        } catch (Exception e) {
            System.out.println("[[[[[[   MSG   Invocation of destroy method failed on bean with name '" + this.beanName + "'");
        }
    }
}

