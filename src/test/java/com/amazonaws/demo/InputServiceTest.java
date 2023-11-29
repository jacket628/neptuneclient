package com.amazonaws.demo;

import com.amazonaws.demo.bean.GraphClient;
import com.amazonaws.demo.config.AWSServiceConfig;
import com.amazonaws.demo.service.DataLoadWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class InputServiceTest {
    @Autowired
    private AWSServiceConfig awsServiceConfig;
    @Autowired
    @Qualifier("writeClient")
    GraphClient writeClient;

    @Test
    public void stressTest() throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        //1000w个点, 500w条边； 10万次循环，每次50条边，100个点
        int loopCount = 100000;
        CountDownLatch countDownLatch = new CountDownLatch(loopCount);
        AtomicInteger errorCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        log.info("stress test begin submit! time is {}", startTime);
        for (int i = 0; i < loopCount; i++) {
            threadPool.execute(new DataLoadWorker(writeClient, i, 50, errorCount, countDownLatch));
        }

        try {
            //等待计数器归零
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        log.info("stress test finish submit! End time is {}, totoal time is {}, errorCount: {}", endTime, (endTime - startTime) / 1000, errorCount);
    }

}
