package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
// 这个命名是不是一定要和aop里面的保持一致?
public class AutoFillAspect {

    /**
     * 这里自定义切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知，在通知中进行公共字段的赋值
     */

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 这个签名可以拿到函数对象和返回类型
        AutoFill annotation = methodSignature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = annotation.value();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];    // 这个是要插入到数据库里面的参数

        if (operationType == OperationType.INSERT) {
            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setCreateTime.invoke(entity, LocalDateTime.now());
            setCreateUser.invoke(entity, BaseContext.getCurrentId());
            setUpdateTime.invoke(entity, LocalDateTime.now());
            setUpdateUser.invoke(entity, BaseContext.getCurrentId());

        } else if (operationType == OperationType.UPDATE) {
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setUpdateTime.invoke(entity, LocalDateTime.now());
            setUpdateUser.invoke(entity, BaseContext.getCurrentId());
        }
    }

//    @Before("autoFillPointCut()")
//    public void autoFill0(JoinPoint joinPoint){
//        log.info("开始进行公共字段自动填充...");
//
//        //获取到当前被拦截的方法上的数据库操作类型
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
//        // AutoFill 是一个接口,为什么可以搞到这个对象?
//        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
//        // assume we have already got that object, we can get the operation type then
//        OperationType operationType = autoFill.value();//获得数据库操作类型
//
//        //获取到当前被拦截的方法的参数--实体对象
//        // 这个参数应该就是mapper中去set,想要插入到数据库里面的参数,
//        Object[] args = joinPoint.getArgs();
//        if(args == null || args.length == 0){
//            return;
//        }
//
//        Object entity = args[0];
//
//        //准备赋值的数据
//        LocalDateTime now = LocalDateTime.now();
//        Long currentId = BaseContext.getCurrentId();
//
//        //根据当前不同的操作类型，为对应的属性通过反射来赋值
//        if(operationType == OperationType.INSERT){
//            //为4个公共字段赋值
//            try {
//                // entity相当于全体参数,这里相当于拿到对每个方法操作的句柄
//                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
//                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
//                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
//                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
//
//                //通过反射为对象属性赋值
//                setCreateTime.invoke(entity,now);
//                setCreateUser.invoke(entity,currentId);
//                setUpdateTime.invoke(entity,now);
//                setUpdateUser.invoke(entity,currentId);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }else if(operationType == OperationType.UPDATE){
//            //为2个公共字段赋值
//            try {
//                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
//                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
//
//                //通过反射为对象属性赋值
//                setUpdateTime.invoke(entity,now);
//                setUpdateUser.invoke(entity,currentId);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}