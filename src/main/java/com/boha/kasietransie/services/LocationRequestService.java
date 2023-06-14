package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.LocationRequest;
import com.boha.kasietransie.data.dto.LocationResponse;
import com.boha.kasietransie.data.repos.LocationRequestRepository;
import com.boha.kasietransie.data.repos.LocationResponseRepository;
import org.springframework.stereotype.Service;

@Service
public class LocationRequestService {
    private final LocationRequestRepository locationRequestRepository;
    private final LocationResponseRepository locationResponseRepository;
    private final MessagingService messagingService;

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
        LocationResponse r = locationResponseRepository.insert(locationResponse);
        messagingService.sendMessage(r);
        return r;

    }
}
