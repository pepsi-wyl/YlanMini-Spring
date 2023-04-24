package com.ylan.spring.aop.advisor;


// 方法拦截器接口
public interface MethodInterceptor extends Interceptor {
    // invoke 触发目标类方法
    Object invoke(MethodInvocation invocation) throws Throwable;
}
