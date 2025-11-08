package com.rhf.rider.dto;

import java.time.LocalDateTime;

public record TripResponse(
        String tripId,
        String riderId,
        String driverId,
        String status,
        double fare,
        LocalDateTime createdAt
) {}
