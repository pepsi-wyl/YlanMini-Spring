package com.ylan.spring.aop.proxy;


import com.ylan.spring.aop.advisor.DefaultMethodInvocation;
import com.ylan.spring.aop.advisor.Interceptor;
import com.ylan.spring.aop.util.AopUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;


// 使用 JDK 动态代理
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    // ProxyFactory 实际为 ProxyConfig
    private ProxyFactory proxyFactory;

    // 代理对象的接口
    private final Class<?>[] proxiedInterfaces;

    // 构造方法
    public JdkDynamicAopProxy(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
        this.proxiedInterfaces = completeProxiedInterfaces(this.proxyFactory);
    }

    // 补充代理对象的接口，如 SpringProxy、Advised、DecoratingProxy
    private Class<?>[] completeProxiedInterfaces(ProxyFactory proxyFactory) {
        Class<?>[] proxiedInterfaces = proxyFactory.getProxiedInterfaces();
        return proxiedInterfaces;
    }

    // 获取代理类
    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),  // 类加载器
                this.proxiedInterfaces,                          // 实现类的接口
                this            // InvocationHandler接口实例 invoke(Object proxy, Method method, Object[] args) 处理代理的实例
        );
    }

    // 实现 InvocationHandler 接口, 重写 invoke 方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        TargetSource targetSource = this.proxyFactory.getTargetSource(); // 获取需要代理的对象目标源
        Object target = null;                                            // 需要代理的对象

        boolean setProxyContext = false; // setProxyContext 默认为False  可能存在你不可以暴露当前正在运行的代理对象给 AopContext
        Object oldProxy = null;          // Store or Restore old proxy.

        try {

            Object retVal;

            // 当前对象允许暴露当前正在运行的代理对象给AopContext
            if (this.proxyFactory.exposeProxy()) {
                oldProxy = AopContext.setCurrentProxy(proxy);  // Store old proxy.
                setProxyContext = true;                        // setProxyContext 改为 True
            }

            target = targetSource.getTarget();        // 对象目标源获取目标对象
            Class<?> targetClass = target.getClass(); // 目标对象获取对象Class

            // 获取此 method 拦截器链
            List<Interceptor> chain = this.proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

            // 执行 chain     拦截器为空，直接调用切点方法
            if (chain.isEmpty()) {
                // 触发目标类方法 return method.invoke(target, args);
                retVal = AopUtils.invokeJoinpointUsingReflection(target, method, args);
            }
            // 执行 chain     拦截器不为空，将拦截器统一封装成DefaultMethodInvocation
            else {
                // 将拦截器统一封装成DefaultMethodInvocation
                DefaultMethodInvocation methodInvocation = new DefaultMethodInvocation(target, method, args, chain);
                // 执行拦截器链
                retVal = methodInvocation.proceed();
            }

            // 处理特殊的返回值 this
            Class<?> returnType = method.getReturnType();
            if (retVal != null && retVal == target && returnType != Object.class && returnType.isInstance(proxy)) {
                retVal = proxy;
            }

            return retVal;

        } finally {

            // setProxyContext 为 True
            if (setProxyContext) {
                // Restore old proxy.
                AopContext.setCurrentProxy(oldProxy);
            }
        }
    }
}
