package com.haiyisoft.xcc;

import com.haiyisoft.ivr.IvrHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync(proxyTargetClass = true)//开启异步任务支持
@SpringBootApplication
@ComponentScan("com.haiyisoft.**")
public class XccApplication {

    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(XccApplication.class, args);
//        IvrHandler ivr = context.getBean("ivrHandler", IvrHandler.class);
//        for (int i = 0; i < 10000; i++) {
//
//            ivr.ivrDomain(null, null);
//        }

    }

}
