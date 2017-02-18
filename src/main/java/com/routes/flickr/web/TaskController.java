package com.routes.flickr.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.routes.flickr.boundary.FindRoutesTask;
import com.routes.flickr.boundary.SaveRoutesTask;
import com.routes.flickr.boundary.TaskBoundary;
import com.routes.flickr.interactor.FlickrRoutesRetriever;
import com.routes.flickr.entity.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.zeromq.ZMQ.Socket;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import static java.lang.Thread.currentThread;

@Controller
public class TaskController implements TaskBoundary {

    @Autowired
    @Qualifier("findRoutesTaskSubscriber")
    private Socket subscriber;

    @Autowired
    @Qualifier("saveRoutesTaskPublisher")
    private Socket publisher;

    @Value("${zeromq.topic.saveRoutes.envelope}")
    private String saveRoutesTaskEnvelope;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FlickrRoutesRetriever flickrRoutesRetriever;

    @PostConstruct
    @Override
    public void subscribeToFindRoutesTaskChannel() {
        objectMapper.registerModule(new JavaTimeModule());
        new Thread(this::processIncomingMessages).start();
    }

    @Override
    public void publishSaveRouteTask(SaveRoutesTask task) {
        publisher.sendMore(saveRoutesTaskEnvelope);
        sendTask(task);
    }

    private void processIncomingMessages() {
        while (!currentThread().isInterrupted()) {
            String taskType = subscriber.recvStr();
            String taskContents = subscriber.recvStr();
            FindRoutesTask findRoutesTask = convert(taskContents);
            List<Route> routes = flickrRoutesRetriever.getRoutesFromFlickr(findRoutesTask.getDate(),
                    findRoutesTask.getDate().plusDays(1),
                    findRoutesTask.getDestination());
            publishSaveRouteTask(new SaveRoutesTask(findRoutesTask.getDate(), routes));
        }
    }

    private FindRoutesTask convert(String contents) {
        try {
            return objectMapper.readValue(contents, FindRoutesTask.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendTask(SaveRoutesTask task) {
        try {
            publisher.send(objectMapper.writeValueAsString(task));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}