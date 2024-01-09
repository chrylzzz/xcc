package com.haiyisoft.xcc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * 模拟ASR并发测试
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
//            new Thread((Runnable) new ASRRequest()).start();

            //1 Callable
            FutureTask ft = new FutureTask<>(new ASRRequest());
            new Thread(ft).start();

            //2 Runnable
            new Thread(new TTSRequest()).start();

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

    public class TTSRequest implements Runnable {

        @Override
        public void run() {
            try {
                COUNTDOWNLATCH.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // TODO: 2024/1/9 business
        }
    }

}
