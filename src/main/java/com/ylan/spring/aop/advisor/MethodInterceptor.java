package com.ylan.spring.aop.advisor;

/**
 * 环绕通知
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 21:17
 */

public interface MethodInterceptor extends Interceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
