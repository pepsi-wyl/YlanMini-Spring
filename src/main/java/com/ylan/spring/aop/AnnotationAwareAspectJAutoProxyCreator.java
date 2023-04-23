package com.ylan.spring.aop;

import com.ylan.spring.YlanApplicationContext;
import com.ylan.spring.aop.advisor.Advice;
import com.ylan.spring.aop.advisor.Advisor;
import com.ylan.spring.aop.advisor.MethodMatcher;
import com.ylan.spring.aop.advisor.Pointcut;
import com.ylan.spring.aop.proxy.ProxyFactory;
import com.ylan.spring.aop.proxy.SingletonTargetSource;
import com.ylan.spring.core.OrderComparator;
import com.ylan.spring.interfaces.ApplicationContextAware;
import com.ylan.spring.interfaces.SmartInstantiationAwareBeanPostProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 在YlanApplicationContext容器 自己注入并且生成
 * bean 后处理器，对符合条件的 bean 进行 aop 代理增强，创建代理对象
 * AOP主入口.....
 */

public class AnnotationAwareAspectJAutoProxyCreator implements SmartInstantiationAwareBeanPostProcessor, ApplicationContextAware {

    // ApplicationContext 上下文
    private YlanApplicationContext applicationContext;

    // 存储的值是 beanName
    // 记录哪些 bean 尝试提前创建代理(不论是否创建了代理增强), 到初始化阶段进行创建代理时，检查缓存，避免重复创建代理
    private final Set<Object> earlyProxyReferences = new HashSet<>();

    // 解析 @Aspect 切面类中的所有切面 的默认实现类
    private final AspectJAdvisorFactory advisorFactory = new DefaultAspectJAdvisorFactory();

    // 切面缓存List
    private List<Advisor> cachedAdvisors;

    // 设置 ApplicationContext 上下文
    @Override
    public void setApplicationContext(YlanApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // 初始化前-直接调用父类的默认方法
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return SmartInstantiationAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    // 初始化后-AOP 就是在这里进行, 返回一个代理对象
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // 如果 Bean 不为空
        if (bean != null) {
            // earlyProxyReferences 中不包含当前 beanName, 创建代理
            if (!this.earlyProxyReferences.contains(beanName)) {
                // 创建代理入口 生成代理类
                return wrapIfNecessary(bean, beanName);
            }
            // earlyProxyReferences   中包含当前 beanName, 不再重复进行代理创建，直接返回
            else {
                // 不生成代理类 直接返回源对象
                this.earlyProxyReferences.remove(beanName);
            }
        }
        return bean;
    }

    // 发生循环依赖时, 提前对 bean 创建代理对象, 注入的就是代理对象，而不是原始对象
    // 如果 bean 需要被代理，返回代理对象；不需要被代理直接返回原始对象
    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws RuntimeException {
        // 在尝试提前创建代理对象集合中添加该Bean
        this.earlyProxyReferences.add(beanName);
        // 调用 wrapIfNecessary 生成代理对象
        return wrapIfNecessary(bean, beanName);
    }

    // 创建代理入口
    private Object wrapIfNecessary(Object bean, String beanName) {
        // 判断是否需要AOP代理 不需要则为True
        if (isInfrastructureClass(bean.getClass())) {
            // 不需要代理直接返回 Bean 即可
            return bean;
        }

        //
        List<Advisor> advisorList = findEligibleAdvisors(bean.getClass(), beanName);

        // 切面List 部不为空
        if (!advisorList.isEmpty()) {
            // 创建代理类 并且返回
            return createProxy(bean.getClass(), bean, beanName, advisorList);
        }
        System.out.println("[[[[[[   MSG   Did not to auto-proxy user class [" + bean.getClass().getName() + "],  beanName[" + beanName + "]");
        return bean;
    }

    // 判断是否需要AOP代理 不需要True
    protected boolean isInfrastructureClass(Class<?> beanClass) {
        // 判断表达式
        boolean retVal =
                Advice.class.isAssignableFrom(beanClass) ||    // 当前类 是否为 通知
                Pointcut.class.isAssignableFrom(beanClass) ||  // 当前类 是否为 切点
                Advisor.class.isAssignableFrom(beanClass) ||   // 当前类 是否为 切面
                this.advisorFactory.isAspect(beanClass);       // 当前类 是否为 切面类
        // logger.trace("Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
        if (retVal) {
            System.out.println("[[[[[[   MSG   Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
        }
        return retVal;
    }

    // 创建代理对象
    private Object createProxy(Class<?> targetClass, Object target, String beanName, List<Advisor> advisorList) {

        // 实际为 ProxyConfig  每个代理对象都持有一个 ProxyFactory, 一个 ProxyFactory 只能生产一个代理对象
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(new SingletonTargetSource(target));  // 设置需要代理的对象目标源
        proxyFactory.setInterfaces(targetClass.getInterfaces());          // 设置接口
        proxyFactory.addAdvisors(advisorList);                            // 设置切面

        System.out.println("[[[[[[   MSG   给 " + beanName + " 创建代理，有 " + advisorList.size() + " 个切面");

        // 调用 proxyFactory 的 getProxy方法 生成并得到代理对象
        return proxyFactory.getProxy();
    }

    private List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
        // 如果最终的 Advisor 列表不为空，再在开头位置添加一个 ExposeInvocationInterceptor
        // extendAdvisors(eligibleAdvisors);
        if (!eligibleAdvisors.isEmpty()) {
            OrderComparator.sort(eligibleAdvisors);
        }
        return eligibleAdvisors;
    }

    private List<Advisor> findCandidateAdvisors() {
        List<Advisor> advisors = findCandidateAdvisorsInBeanFactory();
        advisors.addAll(findCandidateAdvisorsInAspect());
        return advisors;
    }

    private List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
        if (candidateAdvisors.isEmpty()) {
            return candidateAdvisors;
        }
        List<Advisor> eligibleAdvisors = new ArrayList<>(candidateAdvisors.size());
        Method[] methods = beanClass.getDeclaredMethods();

        // 遍历 bean 目标类型的所有方法，包括继承来的接口方法等
        // 继承的方法没写

        // 双重 for 循环
        for (Advisor advisor : candidateAdvisors) {
            MethodMatcher methodMatcher = advisor.getPointcut().getMethodMatcher();
            for (Method method : methods) {
                if (methodMatcher.matches(method, beanClass)) {
                    eligibleAdvisors.add(advisor);
                    break;
                }
            }
        }
        return eligibleAdvisors;
    }

    // 遍历 beanFactory 中所有 bean，找到被 @Aspect 注解标注的 bean，再去 @Aspect 类中封装 Advisor
    private List<Advisor> findCandidateAdvisorsInAspect() {
        if (this.cachedAdvisors != null) {
            return this.cachedAdvisors;
        }
        List<Class<?>> allClass = applicationContext.getAllBeanClass();
        List<Advisor> advisors = new ArrayList<>();

        for (Class<?> cls : allClass) {
            if (this.advisorFactory.isAspect(cls)) {
                List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(cls);
                advisors.addAll(classAdvisors);
            }
        }
        this.cachedAdvisors = advisors;
        return this.cachedAdvisors;
    }

    // 去容器中拿所有低级 Advisor
    private List<Advisor> findCandidateAdvisorsInBeanFactory() {
        return new ArrayList<>();
    }
}
