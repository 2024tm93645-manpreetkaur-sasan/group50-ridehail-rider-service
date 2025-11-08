package com.rhf.rider.client;

import com.rhf.rider.dto.TripRequest;
import com.rhf.rider.dto.TripResponse;

public interface TripClient {

    TripResponse requestTrip(String riderId, TripRequest request);

    TripResponse cancelTrip(String riderId, String tripId);
}
