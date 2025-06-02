package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AutoFillAspect {
    //    指定切入点匹配条件
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPoint() {
    }

    //    通知匹配切入点
    @Before("autoFillPoint()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException {
        log.info("自动填充数据字段");
//        准备数据
        Long currentId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();
//        由于sql操作类型不同,操作也不同,先获取操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill annotation = signature.getMethod().getAnnotation(AutoFill.class);//获取指定注解
        OperationType value = annotation.value();
//        获取实体对象，通过实体对象获取到字节码文件进行反射执行方法
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];

//       通过反射获取方法对象，在根据条件判断执行操作
        Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setCreateTime =entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
        Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
        Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
        if (value.equals(OperationType.INSERT)) {
            try {
                setCreateUser.invoke(entity,currentId);
                setCreateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
            } catch (Exception e) {
               e.printStackTrace();
            }
        }else {
            try {
                setUpdateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
