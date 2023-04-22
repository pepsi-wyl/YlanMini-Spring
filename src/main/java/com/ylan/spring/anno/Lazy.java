package com.ylan.spring.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加在构造方法参数上, 加了 @Lazy 注解的依赖生成代理对象，推迟 bean 的获取
 * 解决构造方法的循环依赖问题
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 20:31
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lazy {
}
