package com.ylan.spring;

import com.ylan.spring.anno.Component;
import com.ylan.spring.anno.ComponentScan;
import com.ylan.spring.anno.Scope;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器类
 *
 * @author by pepsi-wyl
 * @date 2023-04-19 20:34
 */

public class YlanApplicationContext {

    // ConfigClass配置文件
    private final Class<?> configClass;

    // beanName -> BeanDefinition
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    // 单例池 beanName -> beanObj
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    // 构造器
    public YlanApplicationContext(Class<?> configClass) {
        this.configClass = configClass;

        // 扫描 BeanDefinition
        scanBeanDefinition(configClass);

        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            if (beanDefinition.isSingleton()) {
                Object bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        });
    }

    // 扫描 BeanDefinition
    private void scanBeanDefinition(Class<?> configClass) {
        // ComponentScan注解 ------> 扫描路径 ------> 扫描
        if (configClass.isAnnotationPresent(ComponentScan.class)) {

            // 获取ComponentScan的扫描绝对路径
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value().replace(".", "/"); // com/ylan/test
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());  // G:\Coding\java\Spring-Ylan\target\classes\com\ylan\test
            System.out.println("ComponentScan 扫描路径 >>>>>> " + file);

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
                    // 递归调用
                    if (file.isDirectory()) {
                        createBeanDefinition(file);
                    } else {
                        // 文件绝对路径
                        String fileName = file.getAbsolutePath();
                        if (fileName.endsWith(".class")) {
                            // 切割为包名
                            String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class")).replace(File.separator, ".");
                            System.out.println("扫描到 >>>>>> " + className);
                            try {
                                // 反射获取clazz对象
                                ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); // AppClassLoader
                                Class<?> clazz = classLoader.loadClass(className);

                                // 是否有Component注解
                                if (clazz.isAnnotationPresent(Component.class)) {

                                    // beanDefinition对象
                                    BeanDefinition beanDefinition = new BeanDefinition();

                                    // 取出Component注解内容
                                    Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                    String beanName = componentAnnotation.value();

                                    // 判断Scope注解并设置scope的值
                                    if (clazz.isAnnotationPresent(Scope.class)) {
                                        Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                        String scopeValue = scopeAnnotation.value();
                                        beanDefinition.setScope(scopeValue);
                                    } else {
                                        beanDefinition.setScope("singleton");
                                    }

                                    // 设置clazz的值
                                    beanDefinition.setClazz(clazz);

                                    // 放入beanDefinitionMap中
                                    beanDefinitionMap.put(beanName, beanDefinition);

                                    System.out.println("beanDefinitionMap中放入 >>>>>> " + className);
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

    // 创建Bean对象
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {

            Object instance = clazz.getDeclaredConstructor().newInstance();
            return instance;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    // getBean获取Bean对象方法
    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

            if (beanDefinition.isSingleton()) { // 单例
                return singletonObjects.get(beanName);
            } else { // 多例则创建Bean对象
                return createBean(beanName, beanDefinition);
            }

        } else {
            throw new RuntimeException("beanName >>>>>> 不存在");
        }
    }

    // getBean获取Bean对象方法
    public <T> T getBean(String beanName, Class<T> type) {
        return (T) getBean(beanName);
    }

}