package com.routes.flickr.service;

import com.routes.geolocation.client.GoogleGeocodingClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Value("${google.apiKey")
    private String googleApiKey;

    @Bean
    public GoogleGeocodingClient geocodingClient() {
        return new GoogleGeocodingClient(googleApiKey);
    }
}
