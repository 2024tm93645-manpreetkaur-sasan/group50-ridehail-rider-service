package com.rhf.rider.dto;

public record TripRequest(
        String pickupLocation,
        String dropoffLocation,
        String paymentMethod
) {}
