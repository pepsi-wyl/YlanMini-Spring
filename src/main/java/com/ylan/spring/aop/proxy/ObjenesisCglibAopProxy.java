package com.ylan.spring.aop.proxy;

/**
 * TODO 使用 cglib 动态代理
 */
public class ObjenesisCglibAopProxy implements AopProxy {

    private ProxyFactory proxyFactory;

    public ObjenesisCglibAopProxy(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Object getProxy() {
        return null;
    }
}
