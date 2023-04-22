package com.ylan.spring;

import com.ylan.spring.anno.*;
import com.ylan.spring.aop.AnnotationAwareAspectJAutoProxyCreator;
import com.ylan.spring.aop.proxy.LazyInjectTargetSource;
import com.ylan.spring.aop.proxy.ProxyFactory;
import com.ylan.spring.interfaces.*;

import java.io.File;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 容器启动
 * 扫描BeanDefinition
 * Bean的生命周期
 * 单例与多例Bean
 * 依赖注入
 * Aware回调  BeanNameAware ApplicationContextAware
 * InitializingBean
 * BeanPostProcessor
 * AOP
 *
 * @author by pepsi-wyl
 * @date 2023-04-19 20:34
 */

@SuppressWarnings("unchecked")
public class YlanApplicationContext {

    // beanName -> BeanDefinition
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    // 一级缓存 单例池 beanName -> beanObj
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    // 二级缓存 Cache of early singleton objects: bean name to bean instance.
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

    // 三级缓存 Cache of singleton factories: bean name to ObjectFactory.
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

    // BeanPostProcessorList
    private final List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    // Disposable bean instances: bean name to disposable instance.
    private final Map<String, Object> disposableBeans = new LinkedHashMap<>();

    // 当前正在创建的单例对象
    // 它在Bean开始创建时放值，创建完成时会将其移出
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    // 当前正在创建的原型对象
    private final ThreadLocal<Object> prototypesCurrentlyInCreation = new ThreadLocal<>();

    // 构造器
    public YlanApplicationContext(Class<?> configClass) {
        // 扫描Bean生成BeanDefinition放入BeanDefinitionMap中
        scanBeanDefinition(configClass);

        // 创建 BeanPostProcessor  放入 singletonObjects 容器  注册 beanPostProcessorList
        registerBeanPostProcessors();

        // 将扫描到的单例 bean 创建出来放到单例池中
        preInstantiateSingletons();
    }

    // 扫描 BeanDefinition
    private void scanBeanDefinition(Class<?> configClass) {
        // ComponentScan注解 ------> 扫描路径 ------> 扫描
        if (configClass.isAnnotationPresent(ComponentScan.class)) {

            // 获取ComponentScan的扫描绝对路径
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value().replace(".", "/"); // com/ylan/test
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());  // G:\Coding\java\Spring-Ylan\target\classes\com\ylan\test
            System.out.println("[[[[[[   MSG   ComponentScan 扫描路径 >>>>>> " + file);

            // 路径中存在空格等字符，经过 classLoader.getResource 方法后变成了Unicode编码
            String absolutePath = file.getAbsolutePath();
            file = new File(URLDecoder.decode(absolutePath, StandardCharsets.UTF_8));

            // 递归扫描包绝对路径 得到一系列 BeanDefinition 并放入 beanDefinitionMap
            createBeanDefinition(file);
        }
    }

    // 递归扫描包绝对路径 得到一系列 BeanDefinition 并放入 beanDefinitionMap
    private void createBeanDefinition(File srcFile) {
        if (srcFile != null) {
            File[] files = srcFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 文件夹则递归调用
                    if (file.isDirectory()) {
                        createBeanDefinition(file);
                    } else {
                        // 文件的绝对路径 路径+文件名称
                        String fileName = file.getAbsolutePath();

                        // 判断是否为class文件
                        if (fileName.endsWith(".class")) {

                            // 切割为包名
                            String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class")).replace(File.separator, ".");
                            System.out.println("[[[[[[   MSG   扫描到 >>>>>> " + className);

                            try {
                                // 反射获取clazz对象
                                ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); // AppClassLoader
                                Class<?> clazz = classLoader.loadClass(className);

                                // 是否有Component注解
                                if (clazz.isAnnotationPresent(Component.class)) {

                                    // 创建BeanDefinition对象
                                    BeanDefinition beanDefinition = new BeanDefinition();

                                    // 设置clazz的值
                                    beanDefinition.setClazz(clazz);

                                    // 判断是否存在Scope注解并设置scope的值
                                    if (clazz.isAnnotationPresent(Scope.class)) {
                                        Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                        String scopeValue = scopeAnnotation.value();
                                        beanDefinition.setScope(scopeValue);
                                    } else {
                                        beanDefinition.setScope("singleton");
                                    }

                                    // 取出Component注解内容 - beanName
                                    Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                    String beanName = componentAnnotation.value();
                                    // beanName 为 "" 时候
                                    if ("".equals(beanName)) {
                                        // 获取类名的字符数组
                                        char[] chars = clazz.getSimpleName().toCharArray();
                                        // 首字母为大写
                                        if (chars[0] >= 65 && chars[0] <= 90) {
                                            chars[0] += 32;
                                        }
                                        // 类名小写为BeanName
                                        beanName = new String(chars);
                                    }

                                    // 放入beanDefinitionMap中
                                    beanDefinitionMap.put(beanName, beanDefinition);

                                    System.out.println("[[[[[[   MSG   beanDefinitionMap中放入 >>>>>> " + className);
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    // 创建所有 BeanPostProcessor，放入 singletonObjects 容器中，并注册到 beanPostProcessorList
    // BeanPostProcessor属于单例，提前创建好了并放入容器，并不会重复创建
    // 在后续的 preInstantiateSingletons() 初始化单例中，会先从容器中获取，获取不到再创建
    private void registerBeanPostProcessors() {

        // 注册常用的 BeanPostProcessor 到 beanDefinitionMap 中
        registerCommonBeanPostProcessor();

        // 遍历beanDefinitionMap
        this.beanDefinitionMap.entrySet().stream()
                // 过滤 BeanPostProcessor
                .filter(
                        (entry) -> BeanPostProcessor.class.isAssignableFrom(entry.getValue().getClazz())
                )
                // 创建 BeanPostProcessor 放入singletonObjects容器
                .forEach(
                        (entry) -> {
                            // BeanPostProcessor 的创建走 bean 的生命周期流程
                            BeanPostProcessor beanPostProcessor = (BeanPostProcessor) getBean(entry.getKey());
                            this.beanPostProcessorList.add(beanPostProcessor);
                        }
                );
    }

    // 注册常用的 BeanPostProcessor 封装成BeanDefinition 添加到 beanDefinitionMap 中
    private void registerCommonBeanPostProcessor() {
        // AOP
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setClazz(AnnotationAwareAspectJAutoProxyCreator.class);
        beanDefinition.setScope("singleton");
        this.beanDefinitionMap.put("internalAutoProxyCreator", beanDefinition);
    }

    // 将扫描到的单例 bean 创建出来放到单例池中
    private void preInstantiateSingletons() {
        this.beanDefinitionMap.forEach(
                (beanName, beanDefinition) -> {
                    if (beanDefinition.isSingleton()) {
                        // 获取Bean - 创建Bean放入单例池中
                        getBean(beanName);
                    }
                }
        );
    }

    // 先扫描创建BeanDefinition再创建实例，而不是边扫描边创建
    // 因为在 createBean 时，要进行依赖注入，需要看看有没有提供某个类的依赖,所以要先扫描后创建
    public Object getBean(String beanName) {

        // 从beanDefinitionMap中获取BeanDefinition对象
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        if (beanDefinition == null) {
            throw new NullPointerException();
        } else {
            // 单例
            if (beanDefinition.isSingleton()) {

                // 从三处缓存中获取单例对象
                Object singletonObject = getSingleton(beanName, true);

                // 三处缓存中都没该bean，create即可
                if (singletonObject == null) {
                    // 创建Bean对象
                    singletonObject = createBean(beanName, beanDefinition);

                    // 放入一级缓存中
                    this.singletonObjects.put(beanName, singletonObject);
                    this.earlySingletonObjects.remove(beanName);
                    this.singletonFactories.remove(beanName);
                }

                // 返回Bean对象
                return singletonObject;
            }
            // 多例
            else {
                // 创建Bean对象
                return createBean(beanName, beanDefinition);
            }
        }
    }

    public <T> T getBean(String beanName, Class<T> requiredType) {
        return (T) getBean(beanName);
    }

    // 尝试依次从 3 处缓存中取
    // allowEarlyReference 是否应该创建早期引用 bean 初始化后应该检查二级缓存是否提前创建了 bean，此时 allowEarlyReference 为 false，只检查到二级缓存即可
    private Object getSingleton(String beanName, boolean allowEarlyReference) {

        // 一级缓存：单例池
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {

            // 二级缓存：提前创建的单例对象池
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {

                // 三级缓存：单例工厂池
                ObjectFactory<?> objectFactory = this.singletonFactories.get(beanName);
                if (objectFactory != null) {
                    // 调用Lambda表达式 生成Bean对象
                    singletonObject = objectFactory.getObject();

                    // 放入二级缓存
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }

        // 返回Bean对象
        return singletonObject;
    }

    // 创建 bean
    // createBean 方法就是在模拟 bean 的声明周期 创建、依赖注入、初始化
    private Object createBean(String beanName, BeanDefinition beanDefinition) {

        // 创建对象前操作 标记当前正在创建的对象 单例或者原型 循环引用检查
        beforeCreation(beanName, beanDefinition);

        try {

            // 首次创建对象 构造器创建
            Object bean = createBeanInstance(beanName, beanDefinition);

            // 创建的对象是单例对象，依赖注入前将工厂对象 fa 存入三级缓存 singletonFactories 中
            // 1.利用Lambda表达式生成Bean对象  2.提前执行AOP
            if (beanDefinition.isSingleton()) {

                System.out.println("[[[[[[   MSG   createBean：Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references");

                // 放入三级缓存
                this.singletonFactories.put(beanName, () -> {
                    // 赋值Bean至exposedObject中
                    Object exposedObject = bean;

                    // 遍历BeanPostProcessor
                    for (BeanPostProcessor beanPostProcessor : YlanApplicationContext.this.beanPostProcessorList) {

                        // 用于发生循环依赖时，提前对 bean 创建代理对象，这样注入的就是代理对象，而不是原始对象
                        if (beanPostProcessor instanceof SmartInstantiationAwareBeanPostProcessor) {
                            // 生成Lambda对象
                            SmartInstantiationAwareBeanPostProcessor smartInstantiationAwareBeanPostProcessor = (SmartInstantiationAwareBeanPostProcessor) beanPostProcessor;
                            exposedObject = smartInstantiationAwareBeanPostProcessor.getEarlyBeanReference(exposedObject, beanName);
                        }
                    }

                    // 返回Lambda对象
                    return exposedObject;
                });

                // 删除二级缓存
                this.earlySingletonObjects.remove(beanName);
            }

            Object exposedObject = bean;

            // 填充属性 依赖注入阶段
            populateBean(beanName, beanDefinition, bean);

            // 初始化
            exposedObject = initializeBean(beanName, beanDefinition, exposedObject);

            // 去二级缓存 earlySingletonObjects 中查看有没有当前 bean，
            // 如果有，说明发生了循环依赖，返回缓存中的 a 对象(可能是代理对象也可能是原始对象，主要看有没有切点匹配到 bean)
            if (beanDefinition.isSingleton()) {
                // false 只检测到二级缓存
                Object earlySingletonReference = getSingleton(beanName, false);
                if (earlySingletonReference != null) {
                    exposedObject = earlySingletonReference;
                }
            }

            // 注册 disposable bean，注意注册的是原始对象，而不是代理对象
            registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);

            // 返回创建的Bean对象
            return exposedObject;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {

            // 创建对象后操作
            afterCreation(beanName, beanDefinition);
        }
    }

    // 创建对象前操作
    private void beforeCreation(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            beforeSingletonCreation(beanName);
        } else {
            beforePrototypeCreation(beanName);
        }
    }

    // 创建对象前操作 单例Bean  查看singletonsCurrentlyInCreation中是否有该Bean 有则报错没有则添加进去
    private void beforeSingletonCreation(String beanName) {
        if (this.singletonsCurrentlyInCreation.contains(beanName)) {
            throw new IllegalStateException("[[[[[[   MSG   Error creating singleton bean with name '" + beanName + "': " + "Requested bean is currently in creation: Is there an unresolvable circular reference?");
        }
        this.singletonsCurrentlyInCreation.add(beanName);
    }

    // 创建对象前操作 原型Bean
    private void beforePrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal != null && (curVal.equals(beanName) || (curVal instanceof Set && ((Set<?>) curVal).contains(beanName)))) {
            throw new IllegalStateException("[[[[[[   MSG   Error creating prototype bean with name '" + beanName + "': " + "Requested bean is currently in creation: Is there an unresolvable circular reference?");
        }
        // 加入 ThreadLocal
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
        } else if (curVal instanceof String) {
            Set<String> beanNameSet = new HashSet<>();
            beanNameSet.add((String) curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
        } else {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.add(beanName);
        }
    }

    // 创建对象后操作
    private void afterCreation(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            afterSingletonCreation(beanName);
        } else {
            afterPrototypeCreation(beanName);
        }
    }

    // 创建对象后操作 单例Bean 查看singletonsCurrentlyInCreation中是否有该Bean 没有则报错有则删除
    private void afterSingletonCreation(String beanName) {
        if (!this.singletonsCurrentlyInCreation.contains(beanName)) {
            // 可能被别的线程修改了
            throw new IllegalStateException("[[[[[[   MSG   Singleton '" + beanName + "' isn't currently in creation");
        }
        this.singletonsCurrentlyInCreation.remove(beanName);
    }

    // 创建对象后操作 原型Bean
    private void afterPrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
        } else if (curVal instanceof Set) {
            Set<String> beanNameSet = (Set<String>) curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }

    // 创建 bean 实例 根据构造器创建
    // 编译时加上 -parameters 参数  反射获取到参数名
    // 编译时加上 -g 参数           使用 ASM 获取到参数名
    private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) throws Throwable {

        // 反射获取class并获取构造器方法
        Class<?> clazz = beanDefinition.getClazz();
        Constructor<?>[] constructors = clazz.getConstructors();

        // 优先使用无参构造
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                // 直接使用无参构造器构造对象
                return constructor.newInstance();
            }
        }

        // 没有无参构造，使用有参构造，随机选一个构造器
        Constructor<?> constructor = constructors[0];
        Parameter[] parameters = constructor.getParameters();        // 参数名数组
        Object[] args = new Object[constructor.getParameterCount()]; // 参数值数组
        for (int i = 0; i < parameters.length; i++) {

            // 当前参数名
            Parameter parameter = parameters[i];

            Object arg = null;
            // ObjectFactory 参数
            if (parameter.getType().equals(ObjectFactory.class)) {
                arg = buildLazyObjectFactory(parameter.getName());
            }
            // 加了 @Lazy 的参数, 生成代理
            else if (parameter.isAnnotationPresent(Lazy.class)) {
                arg = buildLazyResolutionProxy(parameter.getName(), parameter.getType());
            }
            // 不是 ObjectFactory 也没加 @Lazy, 直接从容器中拿
            else {
                arg = getBean(parameter.getName());
            }
            // 参数值
            args[i] = arg;
        }
        // 使用有参构造器创建对象
        return constructor.newInstance(args);
    }

    //
    private Object buildLazyObjectFactory(String requestingBeanName) {
        return new ObjectFactory<Object>() {
            @Override
            public Object getObject() throws RuntimeException {
                return getBean(requestingBeanName);
            }
        };
    }

    // 加了 @Lazy 的参数, 生成代理
    private Object buildLazyResolutionProxy(String requestingBeanName, Class<?> clazz) {
        LazyInjectTargetSource targetSource = new LazyInjectTargetSource(this, requestingBeanName);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(targetSource);
        proxyFactory.setInterfaces(clazz.getInterfaces());
        // 临时的解决方案，JDK 动态代理只能基于接口，要代理的 class 可能本身是个接口，添加进去
        if (clazz.isInterface()) {
            proxyFactory.addInterface(clazz);
        }
        System.out.println("[[[[[[   MSG   使用Lazy有参构造，为 " + requestingBeanName + " 参数创建代理对象");
        return proxyFactory.getProxy();
    }

    // 依赖注入阶段，执行 bean 后处理器的 postProcessProperties 方法
    private void populateBean(String beanName, BeanDefinition beanDefinition, Object bean) throws IllegalAccessException, InvocationTargetException {

        // 获取反射对象
        Class clazz = beanDefinition.getClazz();

        // 解析方法上的 Autowired
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Autowired.class)) {
                // 编译时加上 -parameters 参数  反射获取到参数名
                // 编译时加上 -g 参数           使用 ASM 获取到参数名
                String paramName = method.getParameters()[0].getName();
                method.invoke(bean, getBean(paramName));
            }
        }

        // 解析字段上的 Autowired
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                // 去Spring 容器中找名为 field.getName() 的 bean，赋值给 bean
                field.set(bean, getBean(field.getName()));
            }
        }
    }

    // 初始化阶段，包含：Aware回调、初始化前、初始化、初始化后
    private Object initializeBean(String beanName, BeanDefinition beanDefinition, Object bean) {

        // 各种 Aware 回调
        if (bean instanceof BeanNameAware) {
            ((BeanNameAware) (bean)).setBeanName(beanName);
        }
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) (bean)).setApplicationContext(this);
        }

        // 初始化前
        // TODO  BeanPostProcessor 解析 @PostConstruct 执行初始化方法
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            bean = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
        }

        // 初始化
        // TODO 执行 @Bean(initMethod = “myInit”) 指定的初始化方法（将初始化方法记录在 BeanDefinition 中）
        if (bean instanceof InitializingBean) {
            ((InitializingBean) (bean)).afterPropertiesSet();
        }

        // 初始化后，由 AnnotationAwareAspectJAutoProxyCreator 创建 aop 代理
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
        }

        // 如果有 aop 的话，这里的 bean 返回的是 aop 后的一个代理对象
        return bean;
    }

    // 注册 DisposableBean 注册的是原始对象，而不是代理对象
    private void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton() && DisposableBeanAdapter.hasDestroyMethod(bean, beanDefinition)) {
            this.disposableBeans.put(beanName, new DisposableBeanAdapter(bean, beanName, beanDefinition));
        }
    }

    // 销毁对象
    public void destroyBean(String beanName, Object bean) {
        new DisposableBeanAdapter(bean, bean.getClass().getName(), null).destroy();

        this.disposableBeans.remove(beanName);
        this.singletonObjects.remove(beanName);
        this.earlySingletonObjects.remove(beanName);
        this.singletonFactories.remove(beanName);
    }

    // 关闭工厂
    public void close() {
        destroySingletons();
    }

    // 销毁单例池
    private void destroySingletons() {
        // 删除
        synchronized (this.disposableBeans) {
            Set<Map.Entry<String, Object>> entrySet = this.disposableBeans.entrySet();
            Iterator<Map.Entry<String, Object>> it = entrySet.iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                String beanName = entry.getKey();
                DisposableBean bean = (DisposableBean) entry.getValue();
                try {
                    // 销毁对象
                    bean.destroy();
                } catch (Exception e) {
                    System.out.println("Destruction of bean with name '" + beanName + "' threw an exception：" + e);
                }
                it.remove();
            }
        }
        // Clear all cached singleton instances in this registry.
        this.singletonObjects.clear();
        this.earlySingletonObjects.clear();
        this.singletonFactories.clear();
    }

    // 返回所有BeanClass
    public List<Class<?>> getAllBeanClass() {
        return beanDefinitionMap.values().stream()
                .map(
                        (Function<BeanDefinition, Class<?>>) BeanDefinition::getClazz
                ).collect(Collectors.toList());
    }

    // 返回所有BeanName
    public ArrayList<String> getBeanNames() {
        return new ArrayList<>(beanDefinitionMap.keySet());
    }
}
