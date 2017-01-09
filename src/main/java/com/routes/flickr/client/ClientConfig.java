package com.routes.flickr.client;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Value("${flickr.apiKey}")
    private String flickrApiKey;

    @Value("${flickr.secret}")
    private String secret;

    @Bean
    public Flickr flickr() {
        return new Flickr(flickrApiKey, secret, new REST());
    }
}
