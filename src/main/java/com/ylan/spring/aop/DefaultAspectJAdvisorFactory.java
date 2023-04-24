package com.ylan.spring.aop;

import com.ylan.spring.aop.AspectJAdvisorFactory;
import com.ylan.spring.aop.PrototypeAspectInstanceFactory;
import com.ylan.spring.aop.advisor.*;
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
        // 高级切面转低级切面类
        List<Advisor> list = new ArrayList<>();

        // 提供调用切面方法的类工厂
        PrototypeAspectInstanceFactory aspectInstanceFactory = new PrototypeAspectInstanceFactory(clazz);

        // 遍历该Class中的所有方法，进行解析注解
        for (Method method : clazz.getDeclaredMethods()) {
            // Before
            if (method.isAnnotationPresent(Before.class)) {
                // 切点 pointcut
                String expression = method.getAnnotation(Before.class).value();         // 获取方法上的切点表达式
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();   // 既是 Pointcut, 又是 MethodMatcher
                pointcut.setExpression(expression);                                     // 设置切点表达式

                // 通知 (最终都是环绕通知)
                BeforeAdvice advice = new BeforeAdvice(method, aspectInstanceFactory);  // 设置 Before 通知

                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);         // 切面-(切点,通知)

                list.add(advisor);
            }
            // After
            else if (method.isAnnotationPresent(After.class)) {
                // 切点 pointcut
                String expression = method.getAnnotation(After.class).value();         // 获取方法上的切点表达式
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();  // 既是 Pointcut, 又是 MethodMatcher
                pointcut.setExpression(expression);                                    // 设置切点表达式

                // 通知 （最终都是环绕通知）
                AfterAdvice advice = new AfterAdvice(method, aspectInstanceFactory);   // 设置 After 通知

                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);        // 切面-(切点,通知)

                list.add(advisor);
            }
            // AfterReturning
            else if (method.isAnnotationPresent(AfterReturning.class)) {
                // 切点 pointcut
                String expression = method.getAnnotation(AfterReturning.class).value();  // 获取方法上的切点表达式
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();    // 既是 Pointcut, 又是 MethodMatcher
                pointcut.setExpression(expression);                                      // 设置切点表达式

                // 通知 （最终都是环绕通知）
                AfterReturningAdvice advice = new AfterReturningAdvice(method, aspectInstanceFactory);  // 设置 AfterReturning 通知

                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);          // 切面-(切点,通知)

                list.add(advisor);
            }
            // AfterThrowing
            else if (method.isAnnotationPresent(AfterThrowing.class)) {
                AfterThrowing afterThrowing = method.getAnnotation(AfterThrowing.class); // 获取 AfterThrowing 注解

                // 切点 pointcut
                String expression = afterThrowing.value();                             // 获取方法上的切点表达式
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();  // 既是 Pointcut, 又是 MethodMatcher
                pointcut.setExpression(expression);                                    // 设置切点表达式

                // 通知 （最终都是环绕通知）
                AfterThrowingAdvice advice = new AfterThrowingAdvice(method, aspectInstanceFactory); // 设置 AfterThrowing 通知
                String throwing = afterThrowing.throwing();                                          // 获取异常表达式
                advice.setThrowingName(throwing);                                                    // 通知设置异常表达式

                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);        // 切面-(切点,通知)

                list.add(advisor);
            }
            // Around
            else if (method.isAnnotationPresent(Around.class)) {
                // 异常判断
                if (method.getParameterCount() == 0) {
                    throw new IllegalStateException("[[[[[[   MSG   环绕通知的参数中缺少 ProceedingJoinPoint");
                }
                if (!method.getParameterTypes()[0].equals(ProceedingJoinPoint.class)) {
                    throw new IllegalStateException("[[[[[[   MSG   环绕通知的参数中第一个位置必须是 ProceedingJoinPoint");
                }

                Around around = method.getAnnotation(Around.class);                     // 获取 Around 注解

                // 切点 pointcut
                String expression = around.value();                                     // 获取方法上的切点表达式
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();   // 既是 Pointcut, 又是 MethodMatcher
                pointcut.setExpression(expression);                                     // 设置切点表达式

                // 通知 （最终都是环绕通知）
                AroundAdvice advice = new AroundAdvice(method, aspectInstanceFactory);  // 设置 Around 通知

                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);         // 切面-(切点,通知)

                list.add(advisor);
            }
        }
        return list;
    }
}