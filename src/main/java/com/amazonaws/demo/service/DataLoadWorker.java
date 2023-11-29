package com.amazonaws.demo.service;

import com.amazonaws.demo.bean.GraphClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Slf4j
public class DataLoadWorker implements Runnable {
    private int batchNo;
    private GraphClient graphClient;
    private int nodeCount;
    private AtomicInteger errorCount;
    private CountDownLatch countDownLatch;
    public DataLoadWorker(GraphClient graphClient, int batchNo, int nodeCount, AtomicInteger errorCount, CountDownLatch countDownLatch) {
        this.graphClient = graphClient;
        this.batchNo = batchNo;
        this.nodeCount = nodeCount;
        this.errorCount = errorCount;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        long startTime = System.currentTimeMillis();
        log.info("thread {} starts processing, batchNo:{}, time is {}", threadName, batchNo, startTime);

        Transaction tx = traversal().withRemote(graphClient.getConnection()).tx();
        GraphTraversalSource g = tx.begin();
        try {
            for (int i = 0; i < nodeCount; i++) {
                String id1 = UUID.randomUUID().toString();
                String id2 = UUID.randomUUID().toString();

                g.addV("testNodeA").property(T.id, id1).iterate();
                g.addV("testNodeB").property(T.id, id2).iterate();
                g.addE("testEdge").from(__.V(id1)).to(__.V(id2)).iterate();
            }
            tx.commit();

            long endTime = System.currentTimeMillis();
            log.info("thread {} ends processing, batchNo:{}, time is {}, total is {}, errorCount: {}", threadName, batchNo,  endTime, endTime - startTime, errorCount);
        } catch (Exception e) {
            log.error("thread {} Error processing, batchNo:{}, msg: {}, errorCount: {}", threadName, batchNo, e.getMessage(), errorCount);
            tx.rollback();
            errorCount.incrementAndGet();
        } finally {
            countDownLatch.countDown();
        }
    }

}
