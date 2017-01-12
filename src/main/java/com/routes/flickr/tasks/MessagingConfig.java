package com.routes.flickr.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.zeromq.ZMQ.*;

@Configuration
public class MessagingConfig {

    @Value("${zeromq.topic.findRoutes.address}")
    private String findRoutesTasksTopicAddress;

    @Value("${zeromq.topic.saveRoutes.address}")
    private String saveRoutesTasksTopicAddress;

    @Value("${zeromq.topic.findRoutes.ioThreads}")
    private int findRoutesSubscriberIoThreads;

    @Value("${zeromq.topic.saveRoutes.ioThreads}")
    private int saveRoutesSubscriberIoThreads;

    @Value("${zeromq.topic.findRoutes.envelope}")
    private String findRoutesTaskEnvelope;

    @Bean(destroyMethod = "term")
    public Context findRoutesTasksTopicContext() {
        return context(findRoutesSubscriberIoThreads);
    }

    @Bean(destroyMethod = "term")
    public Context saveRoutesTasksTopicContext() {
        return context(saveRoutesSubscriberIoThreads);
    }

    @Bean(destroyMethod = "close", name = "findRoutesTaskSubscriber")
    public Socket findRoutesTaskSubscriber() {
        Socket subscriber = findRoutesTasksTopicContext().socket(SUB);
        subscriber.connect(findRoutesTasksTopicAddress);
        subscriber.subscribe(findRoutesTaskEnvelope.getBytes());
        return subscriber;
    }

    @Bean(destroyMethod = "close", name = "saveRoutesTaskPublisher")
    public Socket saveRoutesTaskPublisher() {
        Socket socket = saveRoutesTasksTopicContext().socket(PUB);
        socket.bind(saveRoutesTasksTopicAddress);
        return socket;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
