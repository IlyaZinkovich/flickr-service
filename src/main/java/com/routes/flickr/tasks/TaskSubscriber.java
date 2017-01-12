package com.routes.flickr.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routes.admin.api.FindRoutesTask;
import com.routes.admin.api.Route;
import com.routes.admin.api.SaveRoutesTask;
import com.routes.flickr.service.FlickrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import static java.lang.Thread.currentThread;
import static org.zeromq.ZMQ.Socket;

@Component
public class TaskSubscriber {

    @Autowired
    @Qualifier("findRoutesTaskSubscriber")
    private Socket subscriber;

    @Autowired
    private FlickrService flickrService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskPublisher publisher;

    @PostConstruct
    public void subscribe() {
        new Thread(this::processIncomingMessages).start();
    }

    private void processIncomingMessages() {
        while (!currentThread().isInterrupted()) {
            String taskType = subscriber.recvStr();
            String taskContents = subscriber.recvStr();
            FindRoutesTask findRoutesTask = convert(taskContents);
            List<Route> routes = flickrService.getRoutesFromFlickr(findRoutesTask.getDate(),
                    findRoutesTask.getDate().plusDays(1),
                    findRoutesTask.getDestination());
            routes.stream().map(SaveRoutesTask::new).forEach(publisher::publishSaveRouteTask);
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
