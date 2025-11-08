package com.rhf.rider.controller;

import com.rhf.rider.client.TripClient;
import com.rhf.rider.dto.TripRequest;
import com.rhf.rider.dto.TripResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/riders")
public class RiderTripController {

    private static final Logger logger = LoggerFactory.getLogger(RiderTripController.class);

    private final TripClient tripClient;
    private final Counter tripsRequested;
    private final Counter tripsCancelled;

    public RiderTripController(TripClient tripClient, MeterRegistry registry) {
        this.tripClient = tripClient;
        this.tripsRequested = registry.counter("trips_requested_total");
        this.tripsCancelled = registry.counter("trips_cancelled_total");
    }

    @PostMapping("/{riderId}/trips")
    public ResponseEntity<TripResponse> requestTrip(
            @PathVariable String riderId,
            @RequestBody TripRequest request) {

        logger.info("Rider {} requested trip from {} to {}", riderId, request.pickupLocation(), request.dropoffLocation());
        tripsRequested.increment();

        TripResponse tripResponse = tripClient.requestTrip(riderId, request);
        return ResponseEntity.ok(tripResponse);
    }

    @PostMapping("/{riderId}/trips/{tripId}/cancel")
    public ResponseEntity<TripResponse> cancelTrip(
            @PathVariable String riderId,
            @PathVariable String tripId) {

        logger.info("Rider {} is cancelling trip {}", riderId, tripId);
        tripsCancelled.increment();

        TripResponse tripResponse = tripClient.cancelTrip(riderId, tripId);
        return ResponseEntity.ok(tripResponse);
    }
}
