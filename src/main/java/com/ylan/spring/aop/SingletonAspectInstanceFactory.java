package com.ylan.spring.aop;


import com.ylan.spring.interfaces.AspectInstanceFactory;

// 提供调用切面方法的类工厂, 单例工厂，每次返回相同的对象，计划从容器中拿
public class SingletonAspectInstanceFactory implements AspectInstanceFactory {

    private Object aspectInstance;

    public SingletonAspectInstanceFactory(Object aspectInstance) {
        this.aspectInstance = aspectInstance;
    }

    // 提供调用切面方法的类
    @Override
    public Object getAspectInstance() {
        return this.aspectInstance;
    }
}
