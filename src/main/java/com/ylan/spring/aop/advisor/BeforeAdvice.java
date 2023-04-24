package com.ylan.spring.aop.advisor;

import com.ylan.spring.aop.AspectInstanceFactory;
import com.ylan.spring.aop.advisor.Advice;
import com.ylan.spring.aop.advisor.CommonAdvice;
import com.ylan.spring.aop.advisor.MethodInterceptor;
import com.ylan.spring.aop.advisor.MethodInvocation;

import java.lang.reflect.Method;


// Before 通知类
public class BeforeAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public BeforeAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        before();
        return invocation.proceed();
    }

    public void before() throws Throwable {
        invokeAdviceMethod(null, null);
    }

    // 排序方法
    @Override
    public int getOrder() {
        return 0;
    }
}
