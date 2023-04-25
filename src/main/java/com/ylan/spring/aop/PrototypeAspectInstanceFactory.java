package com.ylan.spring.aop;


import com.ylan.spring.interfaces.AspectInstanceFactory;

// 提供调用切面方法的类工厂, 每次返回新的切面对象
public class PrototypeAspectInstanceFactory implements AspectInstanceFactory {

    private Class<?> clazz;

    public PrototypeAspectInstanceFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    // 提供调用切面方法的类
    @Override
    public Object getAspectInstance() {
        try {
            // 返回实例
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
