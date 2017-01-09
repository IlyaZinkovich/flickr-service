package com.routes.flickr.model;

import java.time.LocalDate;

public class Trip {

    private String origin;
    private LocalDate date;

    public Trip(String origin, LocalDate date) {
        this.origin = origin;
        this.date = date;
    }

    public String getOrigin() {
        return origin;
    }

    public LocalDate getDate() {
        return date;
    }
}