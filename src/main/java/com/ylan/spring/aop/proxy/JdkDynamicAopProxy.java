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

            // 得到此 method 的拦截器链，就是一堆环绕通知
            // 需要根据 invoke 的 method 来做进一步确定，过滤出应用在这个 method 上的 Advice
            List<Interceptor> chain = this.proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

            // 执行 chain
            if (chain.isEmpty()) {
                retVal = AopUtils.invokeJoinpointUsingReflection(target, method, args);
            } else {
                DefaultMethodInvocation methodInvocation = new DefaultMethodInvocation(target, method, args, chain);
                retVal = methodInvocation.proceed();
            }

            // 处理特殊的返回值 this
            Class<?> returnType = method.getReturnType();
            if (retVal != null && retVal == target &&
                    returnType != Object.class && returnType.isInstance(proxy)) {
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
