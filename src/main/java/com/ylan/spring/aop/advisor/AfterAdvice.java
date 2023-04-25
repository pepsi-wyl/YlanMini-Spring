package com.ylan.spring.aop.advisor;

import com.ylan.spring.interfaces.AspectInstanceFactory;

import java.lang.reflect.Method;


// After 通知注解
public class AfterAdvice extends CommonAdvice implements Advice, MethodInterceptor {

    public AfterAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, aspectInstanceFactory);
    }

    // invoke 触发目标类方法    拦截器链执行的顺序正时在各个拦截器的invoke方法中实现
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            // 先链式调用
            return invocation.proceed();
        } finally {
            // 继续调用after方法
            after();
        }
    }

    // after方法
    public void after() throws Throwable {
        // 父类-调用通知方法
        invokeAdviceMethod(null, null);
    }

    // 排序方法
    @Override
    public int getOrder() {
        return 1;
    }
}
