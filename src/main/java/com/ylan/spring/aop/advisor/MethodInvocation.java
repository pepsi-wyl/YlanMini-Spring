package com.ylan.spring.aop.advisor;

import java.lang.reflect.Method;


// 方法拦截器调用链接口
public interface MethodInvocation extends Joinpoint {

    // 获得执行链中目标方法的实参
    Object[] getArguments();

    // 修改执行链中目标方法的实参  ProxyMethodInvocation 中的功能，这里直接放在 MethodInvocation 中了，允许修改实参
    void setArguments(Object[] args);

    // 获得执行链中的目标方法
    Method getMethod();
}
