package com.haiyisoft.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 重写异步线程
 * Created By Chr.yl on 2023-02-08.
 *
 * @author Chr.yl
 */
@Slf4j
@Component
public class ThreadPoolConfig implements AsyncConfigurer {

    //获取当前机器的核数
    public static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(CPU_NUM);//核心线程大小
//        taskExecutor.setMaxPoolSize(CPU_NUM * 2);//最大线程大小

//        taskExecutor.setCorePoolSize(CPU_NUM * 3);//核心线程大小
//        taskExecutor.setMaxPoolSize(CPU_NUM * 6);//最大线程大小

//        taskExecutor.setCorePoolSize(CPU_NUM * 4);//核心线程大小
//        taskExecutor.setMaxPoolSize(CPU_NUM * 8);//最大线程大小

        taskExecutor.setCorePoolSize(CPU_NUM * 5);//核心线程大小
//        taskExecutor.setMaxPoolSize(CPU_NUM * 10);//最大线程大小

//        taskExecutor.setCorePoolSize(CPU_NUM * 6);//核心线程大小
        taskExecutor.setMaxPoolSize(CPU_NUM * 12);//最大线程大小

        taskExecutor.setQueueCapacity(500);//队列最大容量
        // 线程池对拒绝任务的处理策略
        // 当提交的任务个数大于QueueCapacity，就需要设置该参数，但spring提供的都不太满足业务场景，可以自定义一个，也可以注意不要超过QueueCapacity即可
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.setKeepAliveSeconds(60);
        //线程名前缀
        taskExecutor.setThreadNamePrefix("Chryl-Thread-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    /*
    Spring 中的ThreadPoolExecutor是借助JDK并发包中的java.util.concurrent.ThreadPoolExecutor来实现的。其中一些值的含义如下：

    int corePoolSize:线程池维护线程的最小数量

    int maximumPoolSize:线程池维护线程的最大数量，线程池中允许的最大线程数，线程池中的当前线程数目不会超过该值。如果队列中任务已满，并且当前线程个数小于maximumPoolSize，那么会创建新的线程来执行任务。

    long keepAliveTime:空闲线程的存活时间TimeUnit

    unit:时间单位，现由纳秒，微秒，毫秒，秒

    waitTermination(long timeout, TimeUnit unit) ：当前线程阻塞，直到等所有已提交的任务（包括正在跑的和队列中等待的）执行完，或者等超时时间到，或者线程被中断抛出异常；全部执行完返回true，超时返回false。也可以用这个方法代替 isTerminated() 进行判断 。

    BlockingQueue workQueue:持有等待执行的任务队列，一个阻塞队列，用来存储等待执行的任务，当线程池中的线程数超过它的corePoolSize的时候，线程会进入阻塞队列进行阻塞等待

    RejectedExecutionHandler handler 线程池的拒绝策略，是指当任务添加到线程池中被拒绝，而采取的处理措施。
    当任务添加到线程池中之所以被拒绝，可能是由于：第一，线程池异常关闭。第二，任务数量超过线程池的最大限制。
    Reject策略预定义有四种：
    ThreadPoolExecutor.AbortPolicy策略，是默认的策略,处理程序遭到拒绝将抛出运行时 RejectedExecutionException
    ThreadPoolExecutor.CallerRunsPolicy策略 ,调用者的线程会执行该任务,如果执行器已关闭,则丢弃.
    ThreadPoolExecutor.DiscardPolicy策略，不能执行的任务将被丢弃.
    ThreadPoolExecutor.DiscardOldestPolicy策略，如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程）
    自定义策略：当然也可以根据应用场景需要来实现RejectedExecutionHandler接口自定义策略。如记录日志或持久化不能处理的任务

     */

}
