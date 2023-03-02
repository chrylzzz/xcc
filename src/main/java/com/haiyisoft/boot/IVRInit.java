package com.haiyisoft.boot;

import com.haiyisoft.ivr.IVRController;
import com.oracle.tools.packager.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 初始化启动:方法执行时，项目已经初始化完毕
 * Created by Chr.yl on 2023/2/21.
 *
 * @author Chr.yl
 */
@Slf4j
@Component
public class IVRInit {

    @Autowired
    private IVRController ivrController;

    @Bean
    public ApplicationRunner applicationRunner() {
        return applicationArguments -> {
            long startTime = System.currentTimeMillis();
            log.info(Thread.currentThread().getName() + "：开始调用异步业务");
//            ivrController.show();
            ivrController.domain();
            long endTime = System.currentTimeMillis();
            log.info(Thread.currentThread().getName() + "：调用异步业务结束，耗时：" + (endTime - startTime));
        };
    }
}
