package com.routes.flickr.boundary;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

public class FindRoutesTask {

    private String destination;
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}