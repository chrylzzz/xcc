package com.haiyisoft.xcc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Chr.yl on 2024/1/8.
 *
 * @author Chr.yl
 */
@SpringBootTest
public class ChrylConcurrentASRTest {

    @Test
    public void show() {

    }

    private static final int USER_NUMS = 300;
    private static CountDownLatch COUNTDOWNLATCH = new CountDownLatch(USER_NUMS);

    /**
     * 测试并发
     */
    @Test
    public void contextLoads() {
        for (int x = 0; x < USER_NUMS; x++) {
            new Thread((Runnable) new ASRRequest()).start();
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
