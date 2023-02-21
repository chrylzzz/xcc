package com.haiyisoft.boot;

import com.haiyisoft.ivr.IVRController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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

    @Bean
    public ApplicationRunner applicationRunner() {
        return applicationArguments -> {
            long startTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + "：开始调用异步业务");
//            ivrController.show();
            ivrController.domain();
            long endTime = System.currentTimeMillis();
            System.out.println(Thread.currentThread().getName() + "：调用异步业务结束，耗时：" + (endTime - startTime));
        };
    }
}
