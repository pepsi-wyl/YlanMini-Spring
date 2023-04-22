package com.ylan.spring.interfaces;

/**
 * 用于发生循环依赖时，提前对 bean 创建代理对象，这样注入的就是代理对象，而不是原始对象
 * @author by pepsi-wyl
 * @date 2023-04-20 20:33
 */

public interface SmartInstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * 如果 bean 需要被代理，返回代理对象；不需要被代理直接返回原始对象
     * @param bean
     * @param beanName
     * @return
     * @throws RuntimeException
     */
    default Object getEarlyBeanReference(Object bean, String beanName) throws RuntimeException {
        return bean;
    }
}
