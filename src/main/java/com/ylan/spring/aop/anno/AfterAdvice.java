package com.ylan.spring.aop.anno;


import com.ylan.spring.aop.AspectInstanceFactory;
import com.ylan.spring.aop.advisor.Advice;
import com.ylan.spring.aop.advisor.CommonAdvice;
import com.ylan.spring.aop.advisor.MethodInterceptor;
import com.ylan.spring.aop.advisor.MethodInvocation;

import java.lang.reflect.Method;


public class AfterAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public AfterAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } finally {
            after();
        }
    }

    public void after() throws Throwable {
        invokeAdviceMethod(null,null);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
