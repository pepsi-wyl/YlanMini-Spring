# YlanMini-Spring
![img_1.png](img/img_1.png)  
主要实现了 IOC 和 AOP 功能
## IOC
### refresh 流程

1. 根据 @ComponentScan  的包路径递归扫描加了 @Component  注解的类，收集成 BeanDefinition 对象，放入 BeanDefinitionMap。
2. 向容器中注册基本的 BeanPostProcessor，创建 BeanDefinitionMap 中所有的 BeanPostProcessor，放入容器 singletonObjects 中，并添加到 BeanPostProcessorList。
3. 初始化所有单例 bean。
### 实现功能

1. 用 @Component  标记组件，用 @ComponentScan  递归扫描组件，并放入BeanDefinitionMap中。
2. 完成 Bean 生命周期（创建、依赖注入、初始化、销毁）。
3. 依赖注入时解析字段和构造方法上的 @Autowired 注解，从容器中 byName 查找依赖。
4. 使用三级缓存解决「属性注入和 set 方法注入」的循环依赖问题，也解决了涉及注入代理对象的循环依赖问题。
5. 使用 @Lazy  注解、 ObjectFactory 解决构造方法注入的循环依赖问题。
6. 在初始化阶段完成 BeanPostProcessor 的调用、各种 Aware 接口回调。
7. 完成对象的销毁和容器的销毁工作。
## AOP
### 实现功能

1. 完成了 5 种通知类型 @Before、@AfterReturning、@After、@AfterThrowing、@Around  的解析，对符合切点的目标对象进行代理增强。
2. 切点表达式使用 `((String)(expression)).contains(className)` 进行解析，判断通过表示此切面可以应用在这个 class 上，能对这个 class 中所有方法进行增强。
3. 通过 Bean 后处理器 AnnotationAwareAspectJAutoProxyCreator 对符合切点的目标对象进行代理增强。发生循环依赖时在依赖注入阶段提前创建代理，此 Bean 后处理器使用缓存避免了代理对象的重复创建。
4. 应用在目标方法上的多个通知会链式调用执行，且实现了通知的调用顺序控制 Advisor 切面排序。
5. 可以使用 AopContext 获取当前线程正在运行的 AOP 代理对象。
## 使用到的设计模式

- 单例(比较器)
- 工厂(bjectFactory)
- 代理(JDK 生成动态代理对象)
- 责任链(通知的链式调用)
- 适配器(适配各种销毁方法的调用)

## 致谢
Spring IOC源码解析 https://blog.csdn.net/chaitoudaren/category_9799707.html  
Spring AOP源码解析 https://blog.csdn.net/chaitoudaren/category_9803816.html