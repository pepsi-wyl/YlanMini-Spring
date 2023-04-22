package com.ylan.spring.aop.advisor;

/**
 * @author by pepsi-wyl
 * @date 2023-04-20 21:22
 */

public class DefaultPointcutAdvisor implements Advisor {

    private Pointcut pointcut;
    private Advice advice;

    public DefaultPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = pointcut;
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public int getOrder() {
        return this.advice.getOrder();
    }
}
