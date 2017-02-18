package com.routes.flickr.interactor;

import com.routes.flickr.client.FlickrClient;
import com.routes.flickr.entity.GeoTaggedTrip;
import com.routes.flickr.entity.Place;
import com.routes.flickr.entity.Route;
import com.routes.flickr.entity.Trip;
import com.routes.geolocation.model.GeoObject;
import com.routes.geolocation.provider.GeolocationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class FlickrRoutesRetriever {

    @Autowired
    private FlickrClient flickrClient;

    @Autowired
    private GeolocationProvider geolocationProvider;

    public List<Route> getRoutesFromFlickr(LocalDate startDate, LocalDate endDate, String destinationName) {
        Place destination = convert(geolocationProvider.findGeoObject(destinationName));
        List<Trip> strings = flickrClient.searchTrips(startDate, endDate, destination);
        return strings.stream()
                .map(trip -> new GeoTaggedTrip(convert(geolocationProvider.findGeoObject(trip.getOrigin())),
                        trip.getDate()))
                .filter(trip -> trip.getOrigin().known())
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
