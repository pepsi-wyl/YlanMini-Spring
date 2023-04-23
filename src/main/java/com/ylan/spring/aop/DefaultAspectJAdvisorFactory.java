package com.ylan.spring.aop;

import com.ylan.spring.aop.advisor.Advisor;
import com.ylan.spring.aop.advisor.AspectJExpressionPointcut;
import com.ylan.spring.aop.advisor.DefaultPointcutAdvisor;
import com.ylan.spring.aop.advisor.joinpoint.ProceedingJoinPoint;
import com.ylan.spring.aop.anno.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

// 解析 @Aspect 切面类中的所有切面 的默认实现类
public class DefaultAspectJAdvisorFactory implements AspectJAdvisorFactory {

    // 是否是切面类 @Aspect
    @Override
    public boolean isAspect(Class<?> clazz) {
        return clazz.isAnnotationPresent(Aspect.class);
    }

    // 解析 @Aspect 切面类中的所有切面
    @Override
    public List<Advisor> getAdvisors(Class<?> clazz) {

        // 提供调用切面方法的类
        PrototypeAspectInstanceFactory aspectInstanceFactory = new PrototypeAspectInstanceFactory(clazz);

        // 高级切面转低级切面类
        List<Advisor> list = new ArrayList<>();

        // 遍历解析
        for (Method method : clazz.getDeclaredMethods()) {
            // Before
            if (method.isAnnotationPresent(Before.class)) {
                // 切点 pointcut
                String expression = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                BeforeAdvice advice = new BeforeAdvice(method, aspectInstanceFactory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
            // After
            else if (method.isAnnotationPresent(After.class)) {
                // 切点 pointcut
                String expression = method.getAnnotation(After.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                AfterAdvice advice = new AfterAdvice(method, aspectInstanceFactory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
            // AfterReturning
            else if (method.isAnnotationPresent(AfterReturning.class)) {
                // 切点 pointcut
                String expression = method.getAnnotation(AfterReturning.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                AfterReturningAdvice advice = new AfterReturningAdvice(method, aspectInstanceFactory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
            // AfterThrowing
            else if (method.isAnnotationPresent(AfterThrowing.class)) {
                // 切点 pointcut
                AfterThrowing afterThrowing = method.getAnnotation(AfterThrowing.class);
                String expression = afterThrowing.value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                AfterThrowingAdvice advice = new AfterThrowingAdvice(method, aspectInstanceFactory);
                advice.setThrowingName(afterThrowing.throwing());
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
            // Around
            else if (method.isAnnotationPresent(Around.class)) {
                if (method.getParameterCount() == 0) {
                    throw new IllegalStateException("环绕通知的参数中缺少 ProceedingJoinPoint");
                }
                if (!method.getParameterTypes()[0].equals(ProceedingJoinPoint.class)) {
                    throw new IllegalStateException("环绕通知的参数中第一个位置必须是 ProceedingJoinPoint");
                }
                // 切点 pointcut
                Around around = method.getAnnotation(Around.class);
                String expression = around.value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                // 通知 （最终都是环绕通知）
                AroundAdvice advice = new AroundAdvice(method, aspectInstanceFactory);
                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                list.add(advisor);
            }
        }
        return list;
    }
}
