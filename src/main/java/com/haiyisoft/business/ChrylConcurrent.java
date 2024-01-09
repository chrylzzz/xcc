package com.haiyisoft.business;

import com.haiyisoft.entry.IVREvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

/**
 * Thread have return
 * Created by Chr.yl on 2024/1/9.
 *
 * @author Chr.yl
 */
@Slf4j
@Component
public class ChrylConcurrent {

    public static void main(String[] args) {
        new ChrylConcurrent().show();
    }

    private static final int USER_NUMS = 200;

    private static CountDownLatch COUNTDOWNLATCH = new CountDownLatch(USER_NUMS);

    public void show() {
        for (int i = 0; i < USER_NUMS; i++) {
//            Thread thread = new Thread((Runnable) new ChrylRequest());
//            thread.start();

            FutureTask futureTask = new FutureTask(new ChrylRequest());
            new Thread(futureTask).start();

            COUNTDOWNLATCH.countDown();
        }
    }

    public class ChrylRequest implements Callable {
        @Override
        public Object call() throws Exception {
            try {
                COUNTDOWNLATCH.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // TODO: 2024/1/9 business
//            log.info("----");
            return null;
        }
    }
}
