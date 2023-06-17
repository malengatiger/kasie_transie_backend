package com.boha.kasietransie.services;

import com.boha.kasietransie.controllers.DataController;
import com.boha.kasietransie.data.dto.LocationRequest;
import com.boha.kasietransie.data.dto.LocationResponse;
import com.boha.kasietransie.data.repos.LocationRequestRepository;
import com.boha.kasietransie.data.repos.LocationResponseRepository;
import com.github.davidmoten.geo.GeoHash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LocationRequestService {
    private final LocationRequestRepository locationRequestRepository;
    private final LocationResponseRepository locationResponseRepository;
    private final MessagingService messagingService;
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(LocationRequestService.class);


    public LocationRequestService(LocationRequestRepository locationRequestRepository, LocationResponseRepository locationResponseRepository, MessagingService messagingService) {
        this.locationRequestRepository = locationRequestRepository;
        this.locationResponseRepository = locationResponseRepository;
        this.messagingService = messagingService;
    }

    public LocationRequest addLocationRequest(LocationRequest locationRequest) {
        LocationRequest r = locationRequestRepository.insert(locationRequest);
        messagingService.sendMessage(r);
        return r;

    }
    public LocationResponse addLocationResponse(LocationResponse locationResponse) {
        String geoHash = GeoHash.encodeHash(locationResponse.getPosition().getLatitude(),
                locationResponse.getPosition().getLongitude());
        locationResponse.setGeoHash(geoHash);
        LocationResponse r = locationResponseRepository.insert(locationResponse);
        messagingService.sendMessage(r);
        return r;

    }
}