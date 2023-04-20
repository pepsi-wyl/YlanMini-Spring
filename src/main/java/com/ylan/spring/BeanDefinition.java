package com.ylan.spring;

/**
 * Bean的定义
 * @author by pepsi-wyl
 * @date 2023-04-20 16:36
 */

public class BeanDefinition {

    private Class clazz;
    private String scope;

    // 单例对象
    public boolean isSingleton() {
        return "singleton".equals(scope);
    }

    public Class getClazz() {
        return clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "clazz=" + clazz +
                ", scope='" + scope + '\'' +
                '}';
    }
}
