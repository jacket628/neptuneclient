package com.amazonaws.demo.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tinkerpop.gremlin.driver.GremlinClient;
import org.apache.tinkerpop.gremlin.driver.GremlinCluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import software.amazon.neptune.cluster.ClusterEndpointsRefreshAgent;

@Data
@Slf4j
public class GraphClient {
    private GremlinCluster cluster;
    private GremlinClient client;
    private ClusterEndpointsRefreshAgent refreshAgent;

    private DriverRemoteConnection connection;
    public void close() {
        try {
            refreshAgent.close();
            client.close();
            cluster.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("关闭neptune连接异常：{}", e.getMessage());
        }
    }
    
    public GraphClient(GremlinCluster cluster, GremlinClient client, ClusterEndpointsRefreshAgent refreshAgent, DriverRemoteConnection connection) {
        this.cluster = cluster;
        this.client = client;
        this.refreshAgent = refreshAgent;
        this.connection = connection;
    }
    
    public void refreshConnect() {
        GremlinClient oldClient = this.client;
        this.client = cluster.connect();
        this.connection = DriverRemoteConnection.using(client);
        oldClient.close();
    }
}
