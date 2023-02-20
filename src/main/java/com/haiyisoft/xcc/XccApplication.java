package com.haiyisoft.xcc;

import com.haiyisoft.ivr.IVRController;
import com.haiyisoft.ivr.IVRHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync(proxyTargetClass = true)//开启异步任务支持
@SpringBootApplication
@ComponentScan("com.haiyisoft.**")
public class XccApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(XccApplication.class, args);
        //为什么这里行,是因为这里使用了spring容器里的变量进行操作
//        IVRHandler ivr = context.getBean("IVRHandler", IVRHandler.class);
//        IVRController ivrController = context.getBean("IVRController", IVRController.class);
//
////        ivr.ivrDomain(null, null);
////        ivrController.show();
//
////        for (int i = 0; i < 500; i++) {
////            ivr.ivrDomain(null, null);
////            ivrController.show();
////        }

    }


//    @Autowired
//    private IVRController ivrController;
//
//    /**
//     * 启动成功
//     */
//    @Bean
//    public ApplicationRunner applicationRunner() {
//        return applicationArguments -> {
//            long startTime = System.currentTimeMillis();
//            System.out.println(Thread.currentThread().getName() + "：开始调用异步业务");
//            ivrController.show();
//            long endTime = System.currentTimeMillis();
//            System.out.println(Thread.currentThread().getName() + "：调用异步业务结束，耗时：" + (endTime - startTime));
//        };
//    }

}
