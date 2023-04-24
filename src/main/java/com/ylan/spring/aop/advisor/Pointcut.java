package com.ylan.spring.aop.advisor;


// 切点 使用一个 MethodMatcher 对象来判断某个方法是否有资格用于切面
public interface Pointcut {
    MethodMatcher getMethodMatcher();
}
