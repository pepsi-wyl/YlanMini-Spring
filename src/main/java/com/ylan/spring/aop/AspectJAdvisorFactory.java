package com.ylan.spring.aop;

import com.ylan.spring.aop.advisor.Advisor;

import java.util.List;

// 解析 @Aspect 切面类中的所有切面
public interface AspectJAdvisorFactory {
    // 是否是切面类 @Aspect
    boolean isAspect(Class<?> clazz);

    // 解析 @Aspect 切面类中的所有切面
    List<Advisor> getAdvisors(Class<?> clazz);
}
