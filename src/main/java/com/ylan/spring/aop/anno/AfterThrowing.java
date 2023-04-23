package com.ylan.spring.aop.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// AfterThrowing 通知
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterThrowing {
    String value() default "";

    // 值只能是 ex，或者异常的全限定名，如 java.lang.ClassNotFoundException，如果注解中指定了 throwing 参数，通知方法中第一个参数必须是异常类型
    String throwing() default "";
}
