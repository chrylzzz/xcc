package com.haiyisoft.boot;

import com.haiyisoft.ivr.IVRController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 初始化启动
 * Created by Chr.yl on 2023/2/21.
 *
 * @author Chr.yl
 */
@Component
public class IVRInit {

    @Autowired
    private IVRController ivrController;

    @PostConstruct
    public void ivrRunnerInit() {
        System.out.println("gogogo");
        //没有使用spring容器里的变量,所以不生效
//        IVRHandler ivr = new IVRHandler();
//        ivr.ivrDomain(null, null);
//        IVRController ivrController = new IVRController();
//        ivrController.show();
        //使用spring容器表里生效
//        ivrController.show();
    }


    /**
     * 启动成功
     */
    @Bean
    public ApplicationRunner applicationRunner() {
        return applicationArguments -> {
            long startTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + "：开始调用异步业务");
            ivrController.show();
            long endTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + "：调用异步业务结束，耗时：" + (endTime - startTime));
        };
    }
}
