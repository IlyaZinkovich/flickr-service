package com.routes.flickr.client;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;
import com.routes.admin.api.Place;
import com.routes.flickr.model.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class FlickrClient {

    private static final int FIRST_PAGE = 0;
    private static final int SECOND_PAGE = 1;
    private static final String DATE_TAKEN_EXTRA = "date_taken";

    @Autowired
    private Flickr flickr;

    @Value("${flickr.photosPerPage}")
    private int photosPerPage;

    public List<Trip> searchTrips(LocalDate startDate, LocalDate endDate, Place place) {
        PhotoList<Photo> photos = getPhotos(startDate, endDate, place.getLatitude(), place.getLongitude());
        return getTrips(photos);
    }

    private PhotoList<Photo> getPhotos(LocalDate startDate, LocalDate endDate, double latitude, double longitude) {
        SearchParameters parameters = getSearchParametersForLocation(startDate, endDate, latitude, longitude);
        PhotoList<Photo> photos = searchPhotos(parameters, FIRST_PAGE, photosPerPage);
        for (int page = SECOND_PAGE; page < photos.getPages(); page++) {
            photos.addAll(searchPhotos(parameters, page, photosPerPage));
        }
        return photos;
    }

    private static SearchParameters getSearchParametersForLocation(LocalDate startDate, LocalDate endDate,
                                                                   double latitude, double longitude) {
        SearchParameters parameters = new SearchParameters();
        parameters.setLatitude(String.valueOf(latitude));
        parameters.setLongitude(String.valueOf(longitude));
        parameters.setMinTakenDate(Date.valueOf(startDate));
        parameters.setMaxTakenDate(Date.valueOf(endDate));
        parameters.setExtras(Collections.singleton(DATE_TAKEN_EXTRA));
        return parameters;
    }

    private PhotoList<Photo> searchPhotos(SearchParameters parameters, int page, int perPage) {
        try {
            return flickr.getPhotosInterface().search(parameters, perPage, page);
        } catch (FlickrException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Trip> getTrips(PhotoList<Photo> photos) {
        return photos.stream()
                .collect(toMap(Photo::getOwner, photo -> photo, (photo, owner) -> photo)).values()
                .stream()
                .map(photo -> new Trip(getUserLocation(photo.getOwner()),
                        LocalDate.from(photo.getDateTaken().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())))
                .distinct()
                .filter(trip -> trip.getOrigin() != null)
                .collect(toList());
    }

    private String getUserLocation(User user) {
        User userInfo = getUserInfo(user.getId());
        return userInfo.getLocation();
    }

    private User getUserInfo(String userId) {
        try {
            return flickr.getPeopleInterface().getInfo(userId);
        } catch (FlickrException e) {
            return new User() {
                @Override
                public String getLocation() {
                    return null;
                }
            };
        }
    }
}