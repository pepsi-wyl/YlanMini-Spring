package com.ylan.spring.aop.advisor.joinpoint;


// 使用在通知方法的参数上，用于获取各种信息
// 注意对比 com.ylan.spring.aop.advisor.Joinpoint，这个是切入点方法执行的顶级接口
public interface JoinPoint {
    // 获取执行链中目标方法的实参
    Object[] getArgs();

    // 获取执行链中目标方法的方法名
    String getMethodName();
}
