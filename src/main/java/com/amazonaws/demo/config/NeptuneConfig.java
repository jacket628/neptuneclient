package com.amazonaws.demo.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.demo.bean.GraphClient;
import org.apache.tinkerpop.gremlin.driver.*;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.neptune.cluster.*;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class NeptuneConfig {
    @Autowired
    private AWSServiceConfig serviceConfig;
    @Bean
    public GraphClient writeClient() {
        final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

        EndpointsSelector endpointsSelector = EndpointsType.ClusterEndpoint;
        log.info("clusterId:{}, region:{}", serviceConfig.getClusterId(),serviceConfig.getRegion());
        GetEndpointsFromNeptuneManagementApi fetchStrategy = new GetEndpointsFromNeptuneManagementApi(
                serviceConfig.getClusterId(),
                Collections.singletonList(endpointsSelector),
                serviceConfig.getRegion(),
                //"neptune"
                //IamAuthConfig.DEFAULT_PROFILE
                credentialsProvider
        );

        ClusterEndpointsRefreshAgent refreshAgent = new ClusterEndpointsRefreshAgent(fetchStrategy);

        NeptuneGremlinClusterBuilder builder = NeptuneGremlinClusterBuilder.build()
                .enableSsl(true)
                .enableIamAuth(true)
                .serviceRegion(serviceConfig.getRegion())
                //.addContactPoints("database-1.cluster-cvgtbnpgba04.eu-west-1.neptune.amazonaws.com")
                .addContactPoints(refreshAgent.getAddresses().get(endpointsSelector))
                .minConnectionPoolSize(3)
                .maxConnectionPoolSize(3)
                .maxInProcessPerConnection(32)
                .maxSimultaneousUsagePerConnection(32)
                .port(8182)
                //.iamProfile("neptune")
                //.iamProfile(IamAuthConfig.DEFAULT_PROFILE)
                .credentials(credentialsProvider)
                .refreshOnErrorThreshold(1000)
                .refreshOnErrorEventHandler(() -> refreshAgent.getAddresses().get(endpointsSelector))
                .maxWaitForConnection(20000);

        GremlinCluster cluster = builder.create();
        GremlinClient client = cluster.connect();

        refreshAgent.startPollingNeptuneAPI(
                (OnNewAddresses) addresses -> client.refreshEndpoints(addresses.get(endpointsSelector)),
                serviceConfig.getIntervalSeconds(),
                TimeUnit.SECONDS);

        DriverRemoteConnection connection = DriverRemoteConnection.using(client);
        GraphClient graphClient = new GraphClient(cluster, client, refreshAgent, connection);
        return graphClient;
    }

    @Bean
    public GraphClient readClient() {
        EndpointsSelector endpointsSelector = EndpointsType.ReadReplicas;
        final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();

        GetEndpointsFromNeptuneManagementApi fetchStrategy = new GetEndpointsFromNeptuneManagementApi(
                serviceConfig.getClusterId(),
                Collections.singletonList(endpointsSelector),
                serviceConfig.getRegion(),
                //"neptune"
                //IamAuthConfig.DEFAULT_PROFILE
                credentialsProvider
        );

        ClusterEndpointsRefreshAgent refreshAgent = new ClusterEndpointsRefreshAgent(fetchStrategy);

        NeptuneGremlinClusterBuilder builder = NeptuneGremlinClusterBuilder.build()
                .enableSsl(true)
                .enableIamAuth(true)
                .serviceRegion(serviceConfig.getRegion())
                //.addContactPoints("database-1.cluster-ro-cvgtbnpgba04.eu-west-1.neptune.amazonaws.com")
                .addContactPoints(refreshAgent.getAddresses().get(endpointsSelector))
                .minConnectionPoolSize(3)
                .maxConnectionPoolSize(3)
                .maxInProcessPerConnection(32)
                .maxSimultaneousUsagePerConnection(32)
                .port(8182)
                //.iamProfile("neptune")
                //.iamProfile(IamAuthConfig.DEFAULT_PROFILE)
                .credentials(credentialsProvider)
                .refreshOnErrorThreshold(1000)
                .refreshOnErrorEventHandler(() -> refreshAgent.getAddresses().get(endpointsSelector))
                .maxWaitForConnection(20000);

        GremlinCluster cluster = builder.create();
        GremlinClient client = cluster.connect();

        refreshAgent.startPollingNeptuneAPI(
                (OnNewAddresses) addresses -> client.refreshEndpoints(addresses.get(endpointsSelector)),
                serviceConfig.getIntervalSeconds(),
                TimeUnit.SECONDS);

        DriverRemoteConnection connection = DriverRemoteConnection.using(client);

        GraphClient graphClient = new GraphClient(cluster, client, refreshAgent, connection);
        return graphClient;
    }

    public AWSServiceConfig getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(AWSServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

}
