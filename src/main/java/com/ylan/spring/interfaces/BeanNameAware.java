package com.ylan.spring.interfaces;

/**
 * 将 beanName 传递给 bean  某个bean 实现了这个接口，就能得到它的 beanName
 *
 * @author by pepsi-wyl
 * @date 2023-04-20 19:36
 */

public interface BeanNameAware {
    void setBeanName(String beanName);
}
