package com.haiyisoft.business;

import com.haiyisoft.chryl.ivr.DispatcherIVR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * Thread Simple
 * Created by Chr.yl on 2024/1/8.
 *
 * @author Chr.yl
 */
@Slf4j
@Component
public class ConcurrentTemplate {

    @Autowired
    private DispatcherIVR dispatcherIvr;

    public void concurrentTemplate() {
        contextLoads();
    }

    private static final int USER_NUMS = 300;
    private static CountDownLatch COUNTDOWNLATCH = new CountDownLatch(USER_NUMS);

    /**
     * 测试并发
     */
    public void contextLoads() {
        for (int x = 0; x < USER_NUMS; x++) {
//            new Thread((Runnable) new ASRRequest()).start();

            FutureTask futureTask = new FutureTask(new ASRRequest());
            new Thread(futureTask).start();

            COUNTDOWNLATCH.countDown();
        }
    }

    public class ASRRequest implements Callable {

        @Override
        public Object call() {
            try {
                COUNTDOWNLATCH.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // TODO: 2024/1/8 business

            return null;
        }
    }

}
