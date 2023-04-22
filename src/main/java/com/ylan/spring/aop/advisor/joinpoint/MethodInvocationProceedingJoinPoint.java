package com.ylan.spring.aop.advisor.joinpoint;

import com.ylan.spring.aop.advisor.MethodInvocation;

/**
 * @author by pepsi-wyl
 * @date 2023-04-20 21:20
 */

public class MethodInvocationProceedingJoinPoint implements ProceedingJoinPoint {

    private final MethodInvocation methodInvocation;

    public MethodInvocationProceedingJoinPoint(MethodInvocation mi) {
        this.methodInvocation = mi;
    }

    @Override
    public Object proceed() throws Throwable {
        return this.methodInvocation.proceed();
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        this.methodInvocation.setArguments(args);
        return this.methodInvocation.proceed();
    }

    @Override
    public Object[] getArgs() {
        return this.methodInvocation.getArguments().clone();
    }

    @Override
    public String getMethodName() {
        return this.methodInvocation.getMethod().getName();
    }
}
