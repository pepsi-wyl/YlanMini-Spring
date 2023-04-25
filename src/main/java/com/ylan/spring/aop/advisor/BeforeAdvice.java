package com.ylan.spring.aop.advisor;

import com.ylan.spring.interfaces.AspectInstanceFactory;

import java.lang.reflect.Method;


// Before 通知类
public class BeforeAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public BeforeAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    // invoke 触发目标类方法    拦截器链执行的顺序正时在各个拦截器的invoke方法中实现
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 先调用before方法
        before();
        // 继续链式调用
        return invocation.proceed();
    }

    // before方法
    public void before() throws Throwable {
        // 父类-调用通知方法
        invokeAdviceMethod(null, null);
    }

    // 排序方法
    @Override
    public int getOrder() {
        return 0;
    }
}
