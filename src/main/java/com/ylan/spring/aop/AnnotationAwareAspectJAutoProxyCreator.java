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

    // 存储的值是 beanName 记录哪些 bean 尝试提前创建代理(不论是否创建了代理增强), 到初始化阶段进行创建代理时，检查缓存，避免重复创建代理
    private final Set<Object> earlyProxyReferences = new HashSet<>();

    // 候选Advisor缓存List
    private List<Advisor> cachedAdvisors;

    // 解析 @Aspect 切面类中的所有切面 的默认实现类
    private final AspectJAdvisorFactory aspectJAdvisorFactory = new DefaultAspectJAdvisorFactory();

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

        // 判断该Bean是否能进行AOP代理, 不能为True 切面不能被代理......
        if (isInfrastructureClass(bean.getClass())) {
            // 不能进行AOP代理直接返回该Bean 即可
            System.out.println("[[[[[[   MSG   Did not attempt to auto-proxy infrastructure class [" + bean.getClass().getName() + "]");
            return bean;
        }

        // 找具备条件Advisor 获取切面List
        List<Advisor> advisorList = findEligibleAdvisors(bean.getClass(), beanName);
        if (!advisorList.isEmpty()) {
            // 创建代理类 并且返回
            return createProxy(bean.getClass(), bean, beanName, advisorList);
        }

        // 该Bean没有进行AOP代理
        System.out.println("[[[[[[   MSG   Did not to auto-proxy user class [" + bean.getClass().getName() + "],  beanName[" + beanName + "]");
        return bean;
    }

    // 判断该Bean是否能进行AOP代理, 不能为True 切面不能被代理......
    protected boolean isInfrastructureClass(Class<?> beanClass) {
        // 判断表达式
        boolean retVal =
                Advice.class.isAssignableFrom(beanClass) ||    // 当前类 是否为 通知
                        Pointcut.class.isAssignableFrom(beanClass) ||  // 当前类 是否为 切点
                        Advisor.class.isAssignableFrom(beanClass) ||   // 当前类 是否为 切面
                        this.aspectJAdvisorFactory.isAspect(beanClass);       // 当前类 是否为 切面类
        return retVal;
    }

    // 找具备条件Advisor 获取切面List
    private List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        // 候选Advisor     将当前系统中的切面类的切面逻辑进行封装, 从而得到目标 Advisor
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        // 具备条件Advisor  对获取到的所有Advisor进行判断，看其切面定义是否可以应用到当前bean, 从而得到最终需要应用得 Advisor
        List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);

        // 提供hook方法，用于对目标 Advisor 进行拓展
        // extendAdvisors(eligibleAdvisors);

        // 对需要代理的 Advisor 进行排序
        if (!eligibleAdvisors.isEmpty()) {
            OrderComparator.sort(eligibleAdvisors);
        }

        return eligibleAdvisors;
    }

    // 候选Advisor 将当前系统中的切面类的切面逻辑进行封装, 从而得到目标 Advisor
    private List<Advisor> findCandidateAdvisors() {
        // 候选 Advisor 的 List 工厂, 返回 Advisor 的 List 集合
        List<Advisor> advisors = findCandidateAdvisorsInBeanFactory();
        // 向 Advisor 的 List 集合中添加查询到的所有候选 Advisor
        advisors.addAll(findCandidateAdvisorsInAspect());
        return advisors;
    }

    // 候选 Advisor 的 List 工厂, 返回 Advisor 的 List 集合
    private List<Advisor> findCandidateAdvisorsInBeanFactory() {
        return new ArrayList<>();
    }

    // 候选 Advisor 遍历 beanFactory 中所有 bean，找到被 @Aspect 注解标注的 bean，再去 @Aspect 类中封装 Advisor
    private List<Advisor> findCandidateAdvisorsInAspect() {
        // 从候选Advisor缓存List中取
        if (this.cachedAdvisors != null) {
            return this.cachedAdvisors;
        }

        // 保存所有候选Advisor
        List<Advisor> advisors = new ArrayList<>();
        // 遍历IOC容器中所有BeanClass
        for (Class<?> cls : applicationContext.getAllBeanClass()) {
            // 该Class 是否是切面类 @Aspect
            if (this.aspectJAdvisorFactory.isAspect(cls)) {
                // 解析该Class 中 @Aspect 切面类中的所有切面
                List<Advisor> classAdvisors = this.aspectJAdvisorFactory.getAdvisors(cls);
                // 向集合中添加当前查询到的候选Advisor
                advisors.addAll(classAdvisors);
            }
        }

        // 保存到候选Advisor缓存List中
        this.cachedAdvisors = advisors;
        // 返回候选Advisor缓存List
        return this.cachedAdvisors;
    }

    // 具备条件Advisor 对获取到的所有Advisor进行判断，看其切面定义是否可以应用到当前bean, 从而得到最终需要应用得 Advisor
    private List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
        // 候选Advisor 为空 直接返回空
        if (candidateAdvisors.isEmpty()) {
            return candidateAdvisors;
        }

        // 具备条件Advisor集合
        List<Advisor> eligibleAdvisors = new ArrayList<>(candidateAdvisors.size());

        // 获取当前要代理BeanClass的所有方法
        Method[] methods = beanClass.getDeclaredMethods();
        // 遍历所有的候选Advisor
        for (Advisor advisor : candidateAdvisors) {
            // 候选Advisor  方法匹配器
            MethodMatcher methodMatcher = advisor.getPointcut().getMethodMatcher();
            // 遍历所有的方法
            for (Method method : methods) {
                // 候选Advisor 可以匹配上当前方法
                if (methodMatcher.matches(method, beanClass)) {
                    // 该 候选Advisor 为 具备条件Advisor
                    eligibleAdvisors.add(advisor);
                    break;
                }
            }
        }

        // 返回具备条件Advisor集合
        return eligibleAdvisors;
    }

    // 创建代理对象
    private Object createProxy(Class<?> targetClass, Object target, String beanName, List<Advisor> advisorList) {

        // 实际为 ProxyConfig  每个代理对象都持有一个 ProxyFactory, 一个 ProxyFactory 只能生产一个代理对象
        ProxyFactory proxyFactory = new ProxyFactory();                   // 创建代理工厂
        proxyFactory.setTargetSource(new SingletonTargetSource(target));  // 设置需要代理的对象目标源
        proxyFactory.setInterfaces(targetClass.getInterfaces());          // 设置接口
        proxyFactory.addAdvisors(advisorList);                            // 设置切面

        System.out.println("[[[[[[   MSG   给 " + beanName + " 创建代理，有 " + advisorList.size() + " 个切面");

        // 调用 proxyFactory 的 getProxy方法 生成并得到代理对象
        return proxyFactory.getProxy();
    }
}