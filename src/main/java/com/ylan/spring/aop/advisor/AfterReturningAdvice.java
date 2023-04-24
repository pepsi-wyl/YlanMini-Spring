package com.ylan.spring.aop.advisor;


import com.ylan.spring.aop.AspectInstanceFactory;
import com.ylan.spring.aop.advisor.Advice;
import com.ylan.spring.aop.advisor.CommonAdvice;
import com.ylan.spring.aop.advisor.MethodInterceptor;
import com.ylan.spring.aop.advisor.MethodInvocation;

import java.lang.reflect.Method;

// AfterReturning 通知类
public class AfterReturningAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public AfterReturningAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        afterReturning();
        return retVal;
    }

    public void afterReturning() throws Throwable {
        invokeAdviceMethod(null,null);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
