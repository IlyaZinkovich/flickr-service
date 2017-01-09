package com.routes.flickr;

import com.routes.flickr.client.ClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = ClientConfig.class)
public class FlickrApp {

    public static void main(String[] args) {
        SpringApplication.run(FlickrApp.class, args);
    }
}
