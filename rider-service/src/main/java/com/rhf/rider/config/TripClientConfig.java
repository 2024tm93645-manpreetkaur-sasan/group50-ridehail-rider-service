// src/main/java/com/rhf/rider/config/TripClientConfig.java
package com.rhf.rider.config;

import com.rhf.rider.client.TripClient;
import com.rhf.rider.client.impl.TripClientMockImpl;
import com.rhf.rider.client.impl.TripClientRestImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TripClientConfig {

    @Value("${trip.service.base-url:http://trip-service:9083/v1/trips}")
    private String tripServiceBaseUrl;

    @Value("${spring.profiles.active:mock}")
    private String activeProfile;

    @Bean
    public TripClient tripClient() {
        // For now, docker/minikube also use the mock
        if (activeProfile.equalsIgnoreCase("mock") ||
                activeProfile.equalsIgnoreCase("docker") ||
                activeProfile.equalsIgnoreCase("minikube")) {

            return new TripClientMockImpl();
        }

        // Future production or integration use REST client
        return new TripClientRestImpl(tripServiceBaseUrl);
    }
}
