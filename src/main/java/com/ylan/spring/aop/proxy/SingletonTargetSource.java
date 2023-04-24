package com.ylan.spring.aop.proxy;


// 从这个目标源取得的目标对象是单例 做对象池和多例
public class SingletonTargetSource implements TargetSource {

    private final Object target;

    public SingletonTargetSource(Object target) {
        this.target = target;
    }

    @Override
    public Object getTarget() throws Exception {
        return this.target;
    }
}
