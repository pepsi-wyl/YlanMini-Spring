package com.ylan.spring.aop.advisor;

import com.ylan.spring.core.Ordered;


// 切面接口 Spring 中此接口并没有实现 Ordered，而是使用别的方法进行排序
public interface Advisor extends Ordered {
    // 此方法应该再封装一个接口：PointcutAdvisor，放在这个接口里，这里直接放在 Advisor 接口 里了
    // 获取切点
    Pointcut getPointcut();

    // 获取通知
    Advice getAdvice();
}
