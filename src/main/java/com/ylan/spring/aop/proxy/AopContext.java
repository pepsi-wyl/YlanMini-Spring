package com.ylan.spring.aop.proxy;


// 获取当前正在运行的 AOP 代理对象
public final class AopContext {

    public static final ThreadLocal<Object> currentProxy = new ThreadLocal<>();

    private AopContext() {
    }

    // 获取当前正在运行的代理
    public static Object currentProxy() {
        // 获取当前正在运行的代理
        Object proxy = currentProxy.get();
        // 当前没有正在运行的代理
        if (proxy == null) {
            // 抛异常
            throw new IllegalStateException("[[[[[[   MSG   当前没有代理在运行");
        }
        // 返回运行的代理对象
        return proxy;
    }

    // 设置当前正在运行的代理
    public static Object setCurrentProxy(Object proxy) {
        // 获取当前正在运行的代理
        Object old = currentProxy.get();

        if (proxy != null) {
            currentProxy.set(proxy);
        } else {
            currentProxy.remove();
        }

        return old;
    }

}
