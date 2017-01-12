package com.routes.flickr.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.zeromq.ZMQ.*;

@Configuration
public class SubscriberConfig {

    @Value("${zeromq.address}")
    private String address;

    @Value("${zeromq.ioThreads}")
    private int ioThreads;

    @Value("${zeromq.topic.envelope.findRoutes}")
    private String findRoutesTaskEnvelope;

    @Bean(destroyMethod = "term")
    public Context zmqContext() {
        return context(ioThreads);
    }

    @Bean(destroyMethod = "close")
    public Socket zmqSubSocket() {
        Socket subscriber = zmqContext().socket(SUB);
        subscriber.connect(address);
        subscriber.subscribe(findRoutesTaskEnvelope.getBytes());
        return subscriber;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
