package com.ylan.spring.aop.advisor;

/**
 * 切点 使用一个 MethodMatcher 对象来判断某个方法是否有资格用于切面
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 21:15
 */

public interface Pointcut {
    MethodMatcher getMethodMatcher();
}
