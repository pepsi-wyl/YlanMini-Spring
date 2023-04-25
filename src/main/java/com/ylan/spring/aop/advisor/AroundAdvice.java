package com.ylan.spring.aop.advisor;


import com.ylan.spring.interfaces.AspectInstanceFactory;
import com.ylan.spring.aop.advisor.joinpoint.MethodInvocationProceedingJoinPoint;
import com.ylan.spring.aop.advisor.joinpoint.ProceedingJoinPoint;

import java.lang.reflect.Method;


// Around 通知类
public class AroundAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public AroundAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    // invoke 触发目标类方法    拦截器链执行的顺序正时在各个拦截器的invoke方法中实现
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ProceedingJoinPoint proceedingJoinPoint = getProceedingJoinPoint(invocation);
        return around(proceedingJoinPoint);
    }

    // getProceedingJoinPoint
    protected ProceedingJoinPoint getProceedingJoinPoint(MethodInvocation invocation) {
        return new MethodInvocationProceedingJoinPoint(invocation);
    }

    // around方法
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return invokeAdviceMethod(proceedingJoinPoint, null);
    }

    // 排序方法
    @Override
    public int getOrder() {
        return -1;
    }
}
