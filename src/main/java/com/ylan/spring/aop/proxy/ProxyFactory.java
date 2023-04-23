package com.ylan.spring.aop.proxy;

import com.ylan.spring.aop.advisor.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


// 实际为 ProxyConfig  每个代理对象都持有一个 ProxyFactory, 一个 ProxyFactory 只能生产一个代理对象
public class ProxyFactory {

    // 是否代理目标类，即是否所有的代理对象都通过 CGLIB 的方式来创建 默认为False
    private boolean proxyTargetClass = false;

    // 是否允许代理对象作为 ThreadLocal 通过 AopContext 访问    默认为True
    private boolean exposeProxy = true;

    // 被代理的target(目标对象)实例的来源
    private TargetSource targetSource;

    // 切面list
    private List<Advisor> advisorList = new ArrayList<>();

    // 接口List
    private List<Class<?>> interfaces = new ArrayList<>();

    // 空的 Class 数组
    private static final Class<?>[] EMPTY_CLASS_ARRAY = {};


    // 设置需要代理的对象
    public void setTarget(Object bean) {
        setTargetSource(new SingletonTargetSource(bean));
    }

    // 设置需要代理的对象目标源
    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    // 获取需要代理的对象目标源
    public TargetSource getTargetSource() {
        return targetSource;
    }

    // 设置接口
    public void setInterfaces(Class<?>... interfaces) {
        this.interfaces.clear();
        for (Class<?> intf : interfaces) {
            addInterface(intf);
        }
    }

    // 添加接口
    public void addInterface(Class<?> intf) {
        // 不是接口直接抛异常
        if (!intf.isInterface()) {
            throw new IllegalArgumentException("[[[[[[   MSG   [" + intf.getName() + "] is not an interface");
        }
        // 避免重复添加相同的接口
        if (!this.interfaces.contains(intf)) {
            this.interfaces.add(intf);
        }
    }

    // 获取接口
    public Class<?>[] getProxiedInterfaces() {
        return this.interfaces.toArray(EMPTY_CLASS_ARRAY);
    }

    // 添加切面List
    public void addAdvisors(List<Advisor> advisorList) {
        this.advisorList.addAll(advisorList);
    }

    // 是否代理目标类，即是否所有的代理对象都通过 CGLIB 的方式来创建 默认为False
    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    // 设置 是否代理目标类，即是否所有的代理对象都通过 CGLIB 的方式来创建 默认为False
    public void setProxyTargetClass(boolean proxyTargetClass) {
        this.proxyTargetClass = proxyTargetClass;
    }

    // 是否允许代理对象作为 ThreadLocal 通过 AopContext 访问
    public boolean exposeProxy() {
        return exposeProxy;
    }

    // 设置 是否允许代理对象作为 ThreadLocal 通过 AopContext 访问
    public void setExposeProxy(boolean exposeProxy) {
        this.exposeProxy = exposeProxy;
    }

    // 得到代理对象
    public Object getProxy() {
        AopProxy aopProxy = createAopProxy();
        return aopProxy.getProxy();
    }

    // 创建代理对象
    public AopProxy createAopProxy() {
        // 只使用 CGLIB 的方式来创建
        if (isProxyTargetClass()) {
            return new ObjenesisCglibAopProxy(this);
        }
        // 默认为 False
        else {
            // 有接口
            if (!this.interfaces.isEmpty()) {
                return new JdkDynamicAopProxy(this);
            }
            // 没接口
            else {
                return new ObjenesisCglibAopProxy(this);
            }
        }
    }

    // 得到此 method 的拦截器链，就是一堆环绕通知
    // 需要根据 invoke 的 method 来做进一步确定，过滤出应用在这个 method 上的 Advice
    public List<Interceptor> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        List<Interceptor> interceptorList = new ArrayList<>(this.advisorList.size());
        for (Advisor advisor : this.advisorList) {
            MethodMatcher methodMatcher = advisor.getPointcut().getMethodMatcher();
            // 切点表达式匹配才添加此 MethodInterceptor
            if (methodMatcher.matches(method, targetClass)) {
                Advice advice = advisor.getAdvice();
                if (advice instanceof MethodInterceptor) {
                    interceptorList.add((MethodInterceptor) advice);
                }
            }
        }
        return interceptorList;
    }
}
