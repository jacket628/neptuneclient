package com.amazonaws.demo.config;

import com.amazonaws.demo.bean.GraphClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Component
@Slf4j
public class TimerTask {
    @Autowired
    @Qualifier("writeClient")
    GraphClient writeClient;

    @Autowired
    @Qualifier("readClient")
    GraphClient readClient;

    //每10分钟查询一次，保持连接的激活
    @Scheduled(fixedRate = 600000)
    public void refreshQuery() throws  Exception{
        GraphTraversalSource g = traversal().withRemote(readClient.getConnection());

        StringBuilder sb = new StringBuilder();
        try {
            List<Map<Object, Object>> results = g.V().limit(10).valueMap(true).toList();
            for (Map<Object, Object> result : results) {
                sb.append(result.toString()+" ");
            }
        } catch (Exception e) {
            log.error("Error refreshQuery: {}", e.getMessage());
        }

        String res = sb.toString();
        log.info("refreshQuery result:{}", res);
    }

    //每天2点主动获取新连接，防止连接失效
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshConnect() throws  Exception{
        log.info("refresh connection");
        writeClient.refreshConnect();
        readClient.refreshConnect();
    }
}
