package com.routes.flickr.boundary;

import com.routes.flickr.entity.Route;

import java.time.LocalDate;
import java.util.List;

public class SaveRoutesTask {

    private LocalDate updateDate;
    private List<Route> routes;

    public SaveRoutesTask(LocalDate updateDate, List<Route> routes) {
        this.updateDate = updateDate;
        this.routes = routes;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
