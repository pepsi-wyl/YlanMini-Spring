package com.ylan.spring.aop.proxy;

public interface TargetSource {
    Object getTarget() throws Exception;
}
