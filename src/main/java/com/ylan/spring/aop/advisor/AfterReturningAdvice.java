package com.ylan.spring.aop.advisor;


import com.ylan.spring.interfaces.AspectInstanceFactory;

import java.lang.reflect.Method;

// AfterReturning 通知类
public class AfterReturningAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public AfterReturningAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    // invoke 触发目标类方法    拦截器链执行的顺序正时在各个拦截器的invoke方法中实现
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 先链式调用
        Object retVal = invocation.proceed();
        // afterReturning方法
        afterReturning();
        // 返回Invoke值
        return retVal;
    }

    // afterReturning方法
    public void afterReturning() throws Throwable {
        invokeAdviceMethod(null,null);
    }

    // 排序方法
    @Override
    public int getOrder() {
        return 2;
    }
}
