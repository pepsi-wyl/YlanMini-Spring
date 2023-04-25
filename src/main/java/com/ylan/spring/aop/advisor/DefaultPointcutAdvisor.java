package com.ylan.spring.aop.advisor;


// 默认切面实现类
public class DefaultPointcutAdvisor implements Advisor {

    // 切点 pointcut
    private Pointcut pointcut;

    // 通知 (最终都是环绕通知)
    private Advice advice;

    public DefaultPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = pointcut;
        this.advice = advice;
    }

    // 得到切点 pointcut
    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    // 得到通知 (最终都是环绕通知)
    @Override
    public Advice getAdvice() {
        return advice;
    }

    // 得到Order顺序
    @Override
    public int getOrder() {
        return this.advice.getOrder();
    }
}