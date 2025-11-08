package com.rhf.rider.client.impl;

import com.rhf.rider.client.TripClient;
import com.rhf.rider.dto.TripRequest;
import com.rhf.rider.dto.TripResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("!mock") // active in all profiles except "mock"
public class TripClientRestImpl implements TripClient {

    private static final Logger log = LoggerFactory.getLogger(TripClientRestImpl.class);

    private final RestTemplate restTemplate;
    private final String tripServiceBaseUrl;

    public TripClientRestImpl(
            @Value("${trip.service.base-url:http://trip-service:9083/v1/trips}") String tripServiceBaseUrl
    ) {
        this.tripServiceBaseUrl = tripServiceBaseUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public TripResponse requestTrip(String riderId, TripRequest request) {
        String url = tripServiceBaseUrl + "?riderId=" + riderId;
        log.info("Calling Trip Service [POST {}] for rider {}", url, riderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TripRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TripResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, TripResponse.class);

        return response.getBody();
    }

    @Override
    public TripResponse cancelTrip(String riderId, String tripId) {
        String url = tripServiceBaseUrl + "/" + tripId + "/cancel?riderId=" + riderId;
        log.info("Calling Trip Service [POST {}] for trip {}", url, tripId);

        ResponseEntity<TripResponse> response =
                restTemplate.postForEntity(url, null, TripResponse.class);
        return response.getBody();
    }
}
