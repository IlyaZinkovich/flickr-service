package com.routes.flickr.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routes.admin.api.FindRoutesTask;
import com.routes.admin.api.Route;
import com.routes.flickr.service.FlickrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.currentThread;
import static org.zeromq.ZMQ.Socket;

@Component
public class TaskSubscriber {

    @Autowired
    private Socket subscriber;

    @Autowired
    private FlickrService flickrService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void subscribe() {
        new Thread(this::processIncomingMessages).start();
    }

    private void processIncomingMessages() {
        while (!currentThread().isInterrupted()) {
            String taskType = subscriber.recvStr();
            String taskContents = subscriber.recvStr();
            FindRoutesTask findRoutesTask = convert(taskContents);
            List<Route> routes = flickrService.getRoutesFromFlickr(findRoutesTask.getStartDate(),
                    findRoutesTask.getEndDate(),
                    findRoutesTask.getDestination());
        }
    }

    private FindRoutesTask convert(String contents) {
        try {
            return objectMapper.readValue(contents, FindRoutesTask.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
