package com.routes.flickr.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.routes.admin.api.SaveRoutesTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ.Socket;

@Component
public class TaskPublisher {

    @Autowired
    @Qualifier("saveRoutesTaskPublisher")
    private Socket publisher;

    @Value("${zeromq.topic.saveRoutes.envelope}")
    private String saveRoutesTaskEnvelope;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishSaveRouteTask(SaveRoutesTask task) {
        publisher.sendMore(saveRoutesTaskEnvelope);
        sendTask(task);
    }

    private void sendTask(SaveRoutesTask task) {
        try {
            publisher.send(objectMapper.writeValueAsString(task));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
