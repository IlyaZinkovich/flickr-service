package com.routes.flickr.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routes.admin.api.FindRoutesTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ.Socket;

@Component
public class TaskPublisher {

    @Value("${zeromq.topic.envelope.findRoutes}")
    private String findRoutesTaskEnvelope;

    @Autowired
    private Socket publisher;

    @Autowired
    private ObjectMapper objectMapper;

    public void publish(FindRoutesTask findRoutesTask) {
        publisher.sendMore(findRoutesTaskEnvelope);
        sendTask(findRoutesTask);
    }

    private void sendTask(FindRoutesTask findRoutesTask) {
        try {
            publisher.send(objectMapper.writeValueAsString(findRoutesTask));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
