package com.ylan.spring.aop.advisor.joinpoint;

import com.ylan.spring.aop.advisor.MethodInvocation;


// Spring 的实现 MethodInvocationProceedingJoinPoint 中就是内置了一个 MethodInvocation
public class MethodInvocationProceedingJoinPoint implements ProceedingJoinPoint {

    // 方法拦截器调用链
    private final MethodInvocation methodInvocation;

    public MethodInvocationProceedingJoinPoint(MethodInvocation invocation) {
        this.methodInvocation = invocation;
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
