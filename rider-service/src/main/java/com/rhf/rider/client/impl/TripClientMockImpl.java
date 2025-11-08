package com.rhf.rider.client.impl;

import com.rhf.rider.client.TripClient;
import com.rhf.rider.dto.TripRequest;
import com.rhf.rider.dto.TripResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Profile("mock")
public class TripClientMockImpl implements TripClient {

    private static final Logger log = LoggerFactory.getLogger(TripClientMockImpl.class);

    @Override
    public TripResponse requestTrip(String riderId, TripRequest request) {
        log.info("[MOCK] Rider {} requesting trip: {} → {} using {}",
                riderId, request.pickupLocation(), request.dropoffLocation(), request.paymentMethod());

        return new TripResponse(
                "trip-" + UUID.randomUUID(),
                riderId,
                "driver-" + (int) (Math.random() * 1000),
                "CONFIRMED",
                120.50,
                LocalDateTime.now()
        );
    }

    @Override
    public TripResponse cancelTrip(String riderId, String tripId) {
        log.info("[MOCK] Cancelling trip {} for rider {}", tripId, riderId);

        return new TripResponse(
                tripId,
                riderId,
                null,
                "CANCELLED",
                0.0,
                LocalDateTime.now()
        );
    }
}
