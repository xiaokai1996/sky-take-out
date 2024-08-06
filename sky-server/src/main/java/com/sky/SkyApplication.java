package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
/*
1. 如果想要使用@Transactional事务注解，就要使用@EnableTransactionMangament注解，并且加到主启动类或者@Configuration注解的配置类
2. 这个注解需要配合spring-boot-starter-data-jpa依赖使用，这个依赖通常被包含在其他的springboot开头的依赖里面
3. 如果使用了spring-data-jpa，那么不需要显示的开启这个注解
 */
@EnableTransactionManagement //开启注解方式的事务管理
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
