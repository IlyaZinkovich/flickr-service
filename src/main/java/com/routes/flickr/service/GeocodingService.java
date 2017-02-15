package com.routes.flickr.service;

import com.routes.geolocation.client.GoogleGeocodingClient;
import com.routes.geolocation.model.GeoObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeocodingService {

    @Autowired
    private GoogleGeocodingClient geocodingClient;

    private Map<String, GeoObject> geoObjectCache = new ConcurrentHashMap<>();

    public GeoObject findGeoObject(String name) {
        return geoObjectCache.computeIfAbsent(name, k -> geocodingClient.findGeoObject(name));
    }
}
