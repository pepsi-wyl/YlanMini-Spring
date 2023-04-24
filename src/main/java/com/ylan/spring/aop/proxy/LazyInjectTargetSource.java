package com.ylan.spring.aop.proxy;

import com.ylan.spring.YlanApplicationContext;


// 用于构造方法注入时的延迟注入 有可能注入的是个多例 bean，所以每次都从容器中获取，不能缓存起来  做对象池和多例
public class LazyInjectTargetSource implements TargetSource {

    private final YlanApplicationContext applicationContext;
    private final String beanName;

    public LazyInjectTargetSource(YlanApplicationContext applicationContext, String beanName) {
        this.applicationContext = applicationContext;
        this.beanName = beanName;
    }

    @Override
    public Object getTarget() throws Exception {
        return applicationContext.getBean(beanName);
    }
}
