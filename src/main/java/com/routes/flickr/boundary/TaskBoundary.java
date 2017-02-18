package com.routes.flickr.boundary;

public interface TaskBoundary {

    void publishSaveRouteTask(SaveRoutesTask task);
    void subscribeToFindRoutesTaskChannel();
}
