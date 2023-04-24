package com.ylan.spring.aop.advisor;

import com.ylan.spring.aop.util.AopUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author by pepsi-wyl
 * @date 2023-04-20 21:22
 */

public class DefaultMethodInvocation implements MethodInvocation {

    private Object target;  // 代理对象
    private Method method;  // 代理对象方法
    private Object[] args;  // 代理对象方法参数

    List<?> methodInterceptorList; //

    // 调用位置
    private int currentInterceptorIndex = -1;

    public DefaultMethodInvocation(Object target, Method method, Object[] args, List<Interceptor> methodInterceptorList) {
        this.target = target;
        this.method = method;
        if (args == null) {
            this.args = new Object[0];
        } else {
            this.args = args;
        }
        this.methodInterceptorList = methodInterceptorList;
    }

    //
    @Override
    public Object proceed() throws Throwable {
        // 调用目标, 返回并结束递归
        if (this.currentInterceptorIndex == this.methodInterceptorList.size() - 1) {
            // 触发目标类方法 return method.invoke(target, args);
            return invokeJoinpoint();
        }
        // 逐一调用通知, currentInterceptorIndex + 1
        MethodInterceptor methodInterceptor = (MethodInterceptor) this.methodInterceptorList.get(++currentInterceptorIndex);
        return methodInterceptor.invoke(this);
    }

    // 触发目标类方法 return method.invoke(target, args);
    protected Object invokeJoinpoint() throws Throwable {
        return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.args);
    }

    @Override
    public Object[] getArguments() {
        return this.args;
    }

    @Override
    public void setArguments(Object[] args) {
        this.args = args;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }
}
