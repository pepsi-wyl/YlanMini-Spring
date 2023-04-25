package com.ylan.spring.aop.advisor;

import com.ylan.spring.interfaces.AspectInstanceFactory;
import com.ylan.spring.aop.advisor.joinpoint.ProceedingJoinPoint;
import com.ylan.spring.core.Ordered;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


// 公共通知类
public abstract class CommonAdvice implements Advice, Ordered {

    // 通知注解标记的方法
    private Method aspectJAdviceMethod;

    // 切面注解标记的类的Class 调用切面类中方法的类工厂
    private AspectInstanceFactory aspectInstanceFactory;

    // 异常表达式
    // @AfterThrowing( value = "execution()", throwing = "java.lang.ClassNotFoundException") 中 throwing 的值
    private String throwingName;

    // 拦截的异常类型 默认拦截所有异常
    private Class<?> discoveredThrowingType = Object.class;

    public CommonAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        aspectJAdviceMethod.setAccessible(true);
        this.aspectJAdviceMethod = aspectJAdviceMethod;      // 通知注解标记的方法
        this.aspectInstanceFactory = aspectInstanceFactory;  // 切面注解标记的类的Class
    }

    // 调用通知方法，完成简单的参数回显解析
    protected Object invokeAdviceMethod(ProceedingJoinPoint proceedingJoinPoint, Throwable ex) throws Throwable {

        // 准备方法参数
        int parameterCount = this.aspectJAdviceMethod.getParameterCount();
        Object[] args = new Object[parameterCount];

        // 存在异常, @AfterThrowing 通知的调用
        if (ex != null) {
            // 通知方法没有参数
            if (parameterCount == 0) {
                // 设置 throwingName，但通知方法中没有参数，报错  即对应默认的 @AfterThrowing, 注解中不设置 throwing
                if (this.throwingName != null) {
                    throw new IllegalStateException("[[[[[[   MSG   Throwing argument name '" + this.throwingName +
                            "' was not bound in advice arguments");
                }
            }
            // 通知方法有参数
            else {
                // 第一个参数必须是异常类型
                args[0] = ex;
            }
        }

        // 存在 ProceedingJoinPoint, @Around 通知的调用
        if (proceedingJoinPoint != null) {
            // 通知方法没有参数
            if (parameterCount == 0) {
                // 缺少 ProceedingJoinPoint 参数
                throw new IllegalStateException("[[[[[[   MSG   环绕通知的参数中缺少 ProceedingJoinPoint");
            }
            // 通知方法有参数
            else {
                // 第一个参数必须是 ProceedingJoinPoint 类型
                args[0] = proceedingJoinPoint;
            }
        }
        return invokeAdviceMethod(args);
    }

    // 执行切点通知方法
    // AfterThrowing 异常回显 环绕通知会用到通知方法的返回值，其他通知用不到
    private Object invokeAdviceMethod(Object[] args) throws Throwable {
        try {

            // 执行通知方法
            return this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), args);

        } catch (IllegalAccessException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    // 设置异常表达式
    protected void setThrowingName(String name) {
        // ex 表达式
        if (name.equals("ex")) {
            // 设置异常表达式
            this.throwingName = name;

            Class<?> exClass = null;

            // 异常参数判断
            try {
                exClass = this.aspectJAdviceMethod.getParameterTypes()[0];
            } catch (ArrayIndexOutOfBoundsException e) {
                // @AfterThrowing 的 throwing 参数设置为 ex，那么参数列表第 0 个必须是异常类
                throw new IllegalArgumentException("[[[[[[   MSG   方法中缺少异常参数。method = " + this.aspectJAdviceMethod);
            }

            // 异常类型判断
            if (Throwable.class.isAssignableFrom(exClass)) {
                this.discoveredThrowingType = exClass;
            } else {
                // 找不到该异常类
                throw new IllegalArgumentException("[[[[[[   MSG   方法中缺少异常参数，找不到要拦截的异常类型。method = " + this.aspectJAdviceMethod);
            }

        }
        // java.lang.ClassNotFoundException
        else if (name.length() > 0) {
            // 设置异常表达式
            this.throwingName = name;
            // 加载异常类型
            try {
                this.discoveredThrowingType = Class.forName(name);
            } catch (Throwable ex) {
                // 异常类型找不到
                throw new IllegalArgumentException("[[[[[[   MSG   Throwing name '" + name + "' is neither a valid argument name nor the fully-qualified " + "name of a Java type on the classpath. Root cause: " + ex);
            }
        }
        // throwing 没提供，默认拦截所有异常
        else {
            // throwing 没提供，默认拦截所有异常
        }
    }

    // 获取拦截异常的类型
    protected Class<?> getDiscoveredThrowingType() {
        return this.discoveredThrowingType;
    }

}
