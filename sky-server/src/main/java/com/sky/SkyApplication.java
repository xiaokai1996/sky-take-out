package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// 理解几个核心注解

/*
1. 这是一个复合注解，里面包含一些元注解，即注解的注解
@Target指定作用的类型（Type指定类，Field字段声明，Method方法声明）
@Inherited表示注解会被子类继承
@RetentionPolicy指注解保留的范围，（Source保存在源文件，CLASS保存在.class文件，RUNTIME在JVM中，可以在运行时被反射）
@Documented指该类会被javadoc类似的工具文档化
2. 核心注解包括
@SpringBootConfiguration    类似于@Configuration，标记配置类
@EnableAutoConfiguration    根据jar自动配置bean
@ComponentScan  扫描@Component和Controller、Service、Repository等注解的类
 */
@SpringBootApplication
/*
1. 如果想要使用@Transactional事务注解，就要使用@EnableTransactionMangament注解，并且加到主启动类或者@Configuration注解的配置类
2. 这个注解需要配合spring-boot-starter-data-jpa依赖使用，这个依赖通常被包含在其他的springboot开头的依赖里面
3. 如果使用了spring-data-jpa，那么不需要显示的开启这个注解
 */
@EnableTransactionManagement //开启注解方式的事务管理
/*
1. 注意这个注解是由lombok提供的，而且Retention范围是source，只有源码里面有，因此最好在开发环境中安装lombok插件，
lombok里面的@Data也会在源代码基础上自动生成getter和setter方法，范围也是Source，所以在structure里面可以看到@Data注解生成的getter/setter
2. Slf4j是一个框架，具体的实现需要别的日志框架
 */
@Slf4j
/*
1.启动这个注解可以在Service层或者DAO层使用缓存注解，例如Cacheable
2. 需要配置spring-boot-starter-cache依赖，并且需要配置一个具体的实现例如Caffeine，Redis
 */
@EnableCaching//开发缓存注解功能
/*
1. 启用EnableScheduling注解后，会自动搜索@Schduled注解标记的方法
2. 注解里面可以用fixedRate=或者是crontab表达式
 */
@EnableScheduling //开启任务调度
public class SkyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyApplication.class, args);
        log.info("server started");
    }
}
