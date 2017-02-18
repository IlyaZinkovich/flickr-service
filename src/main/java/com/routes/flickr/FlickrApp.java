package com.routes.flickr;

import com.routes.flickr.client.ClientConfig;
import com.routes.flickr.web.WebConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@Import({ClientConfig.class, WebConfig.class})
public class FlickrApp {

    public static void main(String[] args) {
        run(FlickrApp.class, args);
    }
}
