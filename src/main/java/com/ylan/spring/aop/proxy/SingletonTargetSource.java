package com.ylan.spring.aop.proxy;

public class SingletonTargetSource implements TargetSource {

    private final Object target;

    public SingletonTargetSource(Object target) {
        this.target = target;
    }

    @Override
    public Object getTarget() throws Exception {
        return this.target;
    }
}
