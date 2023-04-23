package com.ylan.spring.aop.proxy;


// 被代理的target(目标对象)实例的来源
public interface TargetSource {
    Object getTarget() throws Exception;
}
