package com.ylan.spring.aop.advisor;

import java.lang.reflect.Method;


// 既是 Pointcut, 又是 MethodMatcher
public class AspectJExpressionPointcut implements Pointcut, MethodMatcher {

    // 切点表达式
    private String expression;

    // 设置切点表达式
    public void setExpression(String expression) {
        this.expression = expression;
    }

    // 切点 使用一个 MethodMatcher 对象来判断某个方法是否有资格用于切面
    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    // 判断哪些方法需要被拦截
    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // execution 表达式匹配
        if (expression.startsWith("execution")) {
            return executionMatches(method, targetClass);
        }
        // annotation 表达式匹配
        else if (expression.startsWith("@annotation")) {
            return annotationMatches(method, targetClass);
        }
        // 没有匹配到 抛出异常
        else {
            System.out.println("[[[[[[   MSG   未知 expression，默认返回 true");
            return true;
        }
    }

    // execution 表达式匹配
    private boolean executionMatches(Method method, Class<?> targetClass) {
        // 根据 targetClass 判断该execution表达式是否能匹配------表达式中含有类名即可匹配
        String simpleName = targetClass.getSimpleName();
        if (expression.contains(simpleName)) {
            return true;
        }
        return false;
    }

    // annotation 表达式匹配 为实现  "@annotation(org.springframework.transaction.annotation.Transactional)"
    private boolean annotationMatches(Method method, Class<?> targetClass) {
        return true;
    }
}