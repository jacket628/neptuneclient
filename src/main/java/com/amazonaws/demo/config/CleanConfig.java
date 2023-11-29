package com.amazonaws.demo.config;

import com.amazonaws.demo.bean.GraphClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class CleanConfig implements DisposableBean {
    @Autowired
    @Qualifier("writeClient")
    GraphClient writeClient;
    @Autowired
    @Qualifier("readClient")
    GraphClient readClient;

    @Autowired
    private AWSServiceConfig serviceConfig;

    @PostConstruct
    public void setProperty() {
    }

    @Override
    public void destroy() throws Exception {
        log.info("cluster close begin!");
        writeClient.close();
        readClient.close();
        log.info("cluster close end!");
    }
    
    
    public GraphClient getWriteClient() {
        return writeClient;
    }

    public void setWriteClient(GraphClient writeClient) {
        this.writeClient = writeClient;
    }

    public GraphClient getReadClient() {
        return readClient;
    }

    public void setReadClient(GraphClient readClient) {
        this.readClient = readClient;
    }

    public AWSServiceConfig getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(AWSServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

}
