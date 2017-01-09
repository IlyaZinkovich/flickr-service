package com.routes.flickr.model;

import com.routes.admin.api.Place;

import java.time.LocalDate;

public class GeoTaggedTrip {

    private Place origin;
    private LocalDate date;

    public GeoTaggedTrip(Place origin, LocalDate date) {
        this.origin = origin;
        this.date = date;
    }

    public Place getOrigin() {
        return origin;
    }

    public LocalDate getDate() {
        return date;
    }
}
