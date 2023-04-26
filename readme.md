# YlanMini-Spring
## 简介
Github仓库链接: [https://github.com/pepsi-wyl/YlanMini-Spring](https://github.com/pepsi-wyl/YlanMini-Spring)  
![img_1.png](https://cdn.nlark.com/yuque/0/2023/png/23219042/1682429105899-8a176b27-715f-4670-b259-b13d6d3c8672.png#averageHue=%233f3a37&clientId=u1d344895-139b-4&from=paste&height=194&id=u65546ebf&originHeight=194&originWidth=622&originalType=binary&ratio=1&rotation=0&showTitle=false&size=102437&status=done&style=none&taskId=u96aff9b5-b9dc-4726-bbb9-349550162b5&title=&width=622)  
一个玩具级的仿Spring项目，主要实现了 Spring IOC(依赖注入) 和 Spring AOP(面向切面编程)，实现较Spring源码简单，有助于学习和理解Spring思想和源码。
使用三级缓存解决属性注入和set方法注入的循环依赖问题，@Lazy注解、ObjectFactory 解决构造方法注入的循环依赖问题。
完成了5种通知类型 (@Before、 @AfterReturning、@After、@AfterThrowing、@Around)的解析，对符合切点的目标对象进行代理增强，并对通知进行顺序链式调用。
## IOC
### 实现功能及主要流程

1. 完成组件的扫描，用 @Component注解 标记组件，用 @Scope注解 标记组件作用域，用 @ComponentScan注解 标记要扫描的包， 在容器初始化时递归扫描指定包下的组件，并封装成BeanDefinition，其存储Bean的scope属性和clazz属性，并最后添加到 beanDefinitionMap中，其key为beanName，value为BeanDefinition对象。
2. 完成 BeanPostProcessor 的注册，包括注册 AnnotationAwareAspectJAutoProxyCreator类 来进行AOP自动创建代理和注册用户自定义的 BeanPostProcessor，并对这些组件进行实例化放入 singletonObjects单例池「一级缓存」中，并最后添加到 beanPostProcessorList中保存。
3. 完成 Bean 生命周期「创建、依赖注入、初始化、销毁」。
4. 创建 Bean 对象前，对当前正在创建的 Bean 进行检查，如果当前Bean的实例正在创建则抛出异常，单例Bean 利用 singletonsCurrentlyInCreation集合 中是否存储该Bean，原型Bean则利用ThreadLocal进行检查。
5. 创建 Bean 对象实例阶段，首选无参构造器进行创建对象，没有无参构造器时只能选用有参构造器。选用有参构造器注入时，没有发生循环依赖时直接从容器中取对象依赖注入即可，发生循环依赖时，使用 @Lazy注解 直接生成代理对象或 ObjectFactory 生成三级缓存进行解决。
6. 创建 Bean 对象依赖注入阶段，解析字段和构造方法上的 @Autowired注解，字段上的 @Autowired注解 直接根据字段名称从容器中 byName 查找依赖，构造方法上的 @Autowired注解 直接根据参数名称从容器中 byName 查找依赖然后invoke方法反射注入属性。
7. 创建 Bean 对象初始化阶段，首先完成 各种Aware接口 回调，然后完成 BeanPostProcessor 中 postProcessBeforeInitialization方法 的调用，然后完成 InitializingBean 中 afterPropertiesSet方法 的调用，最后完成 BeanPostProcessor 中 postProcessAfterInitialization方法 的调用，其中postProcessAfterInitialization方法 的调用是执行AOP自动代理的正常入口。
8. 创建 Bean 对象注册DisposableBean阶段，将实现 DisposableBean接口 或者 AutoCloseable接口 的类利用适配器模式封装进DisposableBeanAdapter中并存入 disposableBeans Map中，在对象销毁和容器销毁时直接调用该集合即可。
9. 创建 Bean 对象后，对当前正在创建的 Bean 进行检查，如果当前Bean的实例被其他线程修改则抛出异常，单例Bean 利用 singletonsCurrentlyInCreation集合 中是否存储该Bean，原型Bean则利用ThreadLocal进行检查。
10. 对象的销毁和容器的销毁时，直接调用disposableBeans Map中的对象的销毁方法，如果是容器销毁则需要清空三处缓存。
11. 整个IOC容器使用三级缓存进行设计，解决「属性注入和 set 方法注入」的循环依赖问题，也解决了涉及注入代理对象的循环依赖问题，但循环依赖的两个对象至少有一个是单例，不然会抛出异常。每次在getBean获取对象时，都先执行getSingleton方法从三处缓存中获取，如果在三级缓存中有该对象则执行 Lambda表达式生成代理对象执行AOP流程并放入二级缓存中，如果三处缓存中都没有该对象则进行创建Bean对象。创建Bean时走上述Bean的生命周期流程， 但是创建的Bean都放在三级缓存处(提前曝光)，由于可能在依赖注入的是时候造成循环依赖，一旦发生循环依赖，该类Lambda表达式会执行代理对象生成AOP流程并放入二级缓存中(曝光时机)，创建Bean之后直接放入一级缓存中即可。
### 图解
##### 单例setter循环依赖
![aHR0cHM6Ly91cGxvYWQtaW1hZ2VzLmppYW5zaHUuaW8vdXBsb2FkX2ltYWdlcy84MTkwOTU1LTgzNDZmNmYxZDQ2ZDVjZTcuanBn.png](https://cdn.nlark.com/yuque/0/2023/png/23219042/1682491617628-3d9e0643-b22c-4ebc-95c1-85b35ceb69f8.png#averageHue=%23f2f2f2&clientId=ufecadaa1-fc6d-4&from=drop&id=ud0f9954f&originHeight=1730&originWidth=1978&originalType=binary&ratio=1&rotation=0&showTitle=false&size=590035&status=done&style=none&taskId=u02c971d2-a08b-4e92-a1ee-1c89a655216&title=)
##### 单例构造器注入循环依赖
![aHR0cHM6Ly91cGxvYWQtaW1hZ2VzLmppYW5zaHUuaW8vdXBsb2FkX2ltYWdlcy84MTkwOTU1LTFiNzUwZDliMTc1M2YxNjMuanBn.png](https://cdn.nlark.com/yuque/0/2023/png/23219042/1682491945866-b87957b0-2feb-486b-9d65-8b5530cbdc30.png#averageHue=%23f2f1f1&clientId=ufecadaa1-fc6d-4&from=drop&id=u92ca7a7c&originHeight=1730&originWidth=1978&originalType=binary&ratio=1&rotation=0&showTitle=false&size=593711&status=done&style=none&taskId=u449aa4dc-cd46-4578-8470-8911adcc2f7&title=)
##### AOP遇上循环依赖
![20200403212201923.jpg](https://cdn.nlark.com/yuque/0/2023/jpeg/23219042/1682491032969-bf5926a0-e472-4d99-89b5-da01d3649693.jpeg#averageHue=%23f4eaea&clientId=ufecadaa1-fc6d-4&from=drop&id=ud5ece5db&originHeight=1914&originWidth=2575&originalType=binary&ratio=1&rotation=0&showTitle=false&size=1008577&status=done&style=none&taskId=u1839366e-c05d-4560-b232-fc29634137d&title=)
## AOP
### 实现功能及主要流程

1. 完成切面和通知的定义，使用 @Aspect注解 标记切面，使用 @Before、@AfterReturning、@After、@AfterThrowing、@Around 标记通知，使用切点表达式expression定义切入点，对符合切点的目标对象进行代理增强，应用在目标方法上的多个通知会链式调用执行，且实现了通知的调用顺序控制 Advisor 切面排序。
2. 可以使用 AopContext 获取当前线程正在运行的 AOP 代理对象。
3. 通过 BeanPostProcessor 的实现类 AnnotationAwareAspectJAutoProxyCreator 对符合切点的目标对象进行自动代理增强。发生循环依赖时在依赖注入阶段执行 SmartInstantiationAwareBeanPostProcessor接口 中的 getEarlyBeanReference方法 提前创建代理，在没有发生循环依赖时执行BeanPostProcessor 接口 中的 postProcessAfterInitialization 方法创建代理，使用缓存技术避免了代理对象的重复创建，不论是提前代理还是正常代理都会通过 wrapIfNecessary 方法(创建代理入口)进行创建代理对象。
4. 在 wrapIfNecessary 方法执行时，会去找具备条件的Advisor。找具备条件的Advisor的时候，会先找候选Advisor。在找候选Advisor时，会遍历IOC容器中所有Bean判断是否是 @Aspect注解 标记的类，并调用 DefaultAspectJAdvisorFactory类 中的 getAdvisors方法进行解析，并对每一个通知封装成DefaultPointcutAdvisor 即Pointcut/MethodMatcher和advice，返回候选Advisor集合，其中对候选Advisor使用缓存技术，容器只会加载执行一次。找到候选条件Advisor时，遍历候选Advisor集合执行MethodMatcher接口 的 matches方法 看是否能匹配上该class，返回具备条件的Advisor集合，之后对这些具备条件的Advisor集合利用比较器进行排序。最后执行 createProxy 创建代理。
5. 执行 createProxy 创建代理时，创建 ProxyFactory工厂并执行 getProxy方法。getProxy方法调用 createAopProxy方法，判断代理对象使用JdkDynamicAopProxy 还是 ObjenesisCglibAopProxy，并调用具体实现类的 getProxy方法。JdkDynamicAopProxy 在 getProxy方法中调用Proxy.newProxyInstance方法 生成代理类。
6. JdkDynamicAopProxy 在 invoke方法 中调用 ProxyFactory 的 getInterceptorsAndDynamicInterceptionAdvice方法，把具备条件的Advisor封装成 MethodInterceptor 对象，生成拦截器链 chain(methodInterceptors)。创建 DefaultMethodInvocation对象 并调用 proceed方法，逐一调用MethodInterceptor的 invoke方法，而在MethodInterceptor的实现类中会调用MethodInvocation的 proceed方法，这样根据责任链设计模式执行Advice，达到了增强的目的。
#### 图解
##### 拦截器执行顺序
![20200402204550679.png](https://cdn.nlark.com/yuque/0/2023/png/23219042/1682496493884-72bef4db-32df-4201-9f4a-19ab590dd141.png#averageHue=%23f2f2f2&clientId=ufecadaa1-fc6d-4&from=drop&id=u0969822b&originHeight=636&originWidth=514&originalType=binary&ratio=1&rotation=0&showTitle=false&size=29705&status=done&style=none&taskId=u062b7966-e5eb-4893-b84a-21b131fc997&title=)
##### AOP自动代理时机
![20200406215429755.png](https://cdn.nlark.com/yuque/0/2023/png/23219042/1682496569323-480d90df-ab55-4a61-b936-155b5325364f.png#averageHue=%23f4eded&clientId=ufecadaa1-fc6d-4&from=drop&id=u39fd2b7c&originHeight=2396&originWidth=2092&originalType=binary&ratio=1&rotation=0&showTitle=false&size=574851&status=done&style=none&taskId=uc0b8be19-e7b6-4b6d-9e5e-e8bef3f8ed6&title=)
##### AOP遇上循环依赖
![20200403212201923.jpg](https://cdn.nlark.com/yuque/0/2023/jpeg/23219042/1682496461092-420886a1-b2aa-4d53-aaf4-f79b6de5eadf.jpeg#averageHue=%23f4eaea&clientId=ufecadaa1-fc6d-4&from=drop&id=u069b7fd9&originHeight=1914&originWidth=2575&originalType=binary&ratio=1&rotation=0&showTitle=false&size=1008577&status=done&style=none&taskId=u8b065a02-4f5b-4169-86a7-8ceab7031b5&title=)
## 使用到的设计模式

- 单例(比较器)
- 工厂(ObjectFactory三级缓存)
- 代理(JDK 生成动态代理对象)
- 责任链(通知的链式调用)
- 适配器(适配各种销毁方法的调用)
## 致谢
Spring IOC源码解析 [https://blog.csdn.net/chaitoudaren/category_9799707.html](https://blog.csdn.net/chaitoudaren/category_9799707.html)  
Spring AOP源码解析 [https://blog.csdn.net/chaitoudaren/category_9803816.html](https://blog.csdn.net/chaitoudaren/category_9803816.html)  
