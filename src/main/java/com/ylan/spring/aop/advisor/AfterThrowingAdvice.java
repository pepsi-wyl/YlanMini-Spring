package com.ylan.spring.aop.advisor;


import com.ylan.spring.interfaces.AspectInstanceFactory;

import java.lang.reflect.Method;

// AfterThrowing 通知类
public class AfterThrowingAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public AfterThrowingAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    // 设置异常表达式
    @Override
    public void setThrowingName(String name) {
        super.setThrowingName(name);
    }

    // invoke 触发目标类方法    拦截器链执行的顺序正时在各个拦截器的invoke方法中实现
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            // 先链式调用
            return invocation.proceed();
        } catch (Throwable ex) {
            // 抛出的异常是给定抛出类型的子类型
            if (shouldInvokeOnThrowing(ex)) {
                // 调用异常处理方法
                afterThrowing(ex);
            }
            throw ex;
        }
    }

    // 只有当抛出的异常是给定抛出类型的子类型时，才会调用 afterThrowing 通知
    private boolean shouldInvokeOnThrowing(Throwable ex) {
        return getDiscoveredThrowingType().isAssignableFrom(ex.getClass());
    }

    // afterThrowing方法
    public void afterThrowing(Throwable ex) throws Throwable {
        invokeAdviceMethod(null, ex);
    }

    // 排序方法
    @Override
    public int getOrder() {
        return 2;
    }

}
