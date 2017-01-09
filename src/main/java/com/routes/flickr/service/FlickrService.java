package com.routes.flickr.service;

import com.routes.admin.api.Place;
import com.routes.admin.api.Route;
import com.routes.flickr.client.FlickrClient;
import com.routes.flickr.model.GeoTaggedTrip;
import com.routes.flickr.model.Trip;
import com.routes.geolocation.client.GoogleGeocodingClient;
import com.routes.geolocation.model.GeoObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class FlickrService {

    @Autowired
    private FlickrClient flickrClient;

    @Autowired
    private GoogleGeocodingClient geocodingClient;

    public List<Route> getRoutesFromFlickr(LocalDate startDate, LocalDate endDate, Place destination) {
        List<Trip> strings = flickrClient.searchTrips(startDate, endDate, destination);
        return strings.stream()
                .map(trip -> new GeoTaggedTrip(convert(geocodingClient.findGeoObject(trip.getOrigin())),
                        trip.getDate()))
                .filter(trip -> trip.getOrigin().isKnown())
                .filter(trip -> !samePlace(destination, trip.getOrigin()))
                .map(trip -> new Route(trip.getOrigin(), destination, trip.getDate(), "Flickr"))
                .collect(toList());
    }

    private Place convert(GeoObject geoObject) {
        return new Place(geoObject.getCity(), geoObject.getCountry(),
                geoObject.getLatitude(), geoObject.getLongitude());
    }

    private boolean samePlace(Place destination, Place origin) {
        if (destination.getCountry() != null && origin.getCountry() != null)
            if (!destination.getCountry().equals(origin.getCountry()))
                return false;
        if (destination.getCity() != null && origin.getCity() != null)
            return destination.getCity().equals(origin.getCity());
        return true;
    }
}
