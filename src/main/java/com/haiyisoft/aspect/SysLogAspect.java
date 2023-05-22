package com.haiyisoft.aspect;

import cn.hutool.core.date.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created By Chryl on 2023-03-10.
 */
@Slf4j
@Aspect
@Component
public class SysLogAspect {


    @Around("@annotation(sysLog)")
    public void around(ProceedingJoinPoint joinPoint, com.haiyisoft.anno.SysLog sysLog) throws Throwable {
        String logValue;
        if (sysLog != null) {
            //注解上的描述
            logValue = sysLog.value();
        } else {
            logValue = "NoSuchLog";
        }
        long beginTime = SystemClock.now();
        log.info("{} 执行开始时间为:{}", logValue, LocalDateTime.now());
        //执行方法
        joinPoint.proceed();
        //
        long endTime = SystemClock.now();
        //执行时长(毫秒)
        long time = endTime - beginTime;

        log.warn("{}, {} ：调用业务结束，耗时：{} ms", logValue, Thread.currentThread().getName(), time);
        log.info("{} 执行结束时间为:{}", logValue, LocalDateTime.now());

    }

}
