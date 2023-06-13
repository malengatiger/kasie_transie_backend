package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.UserGeofenceEvent;
import com.boha.kasietransie.data.dto.VehicleGeofenceEvent;
import com.boha.kasietransie.data.repos.UserGeofenceEventRepository;
import com.boha.kasietransie.data.repos.VehicleGeofenceEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class GeofenceService {
    private final VehicleGeofenceEventRepository vehicleGeofenceEventRepository;
    private final UserGeofenceEventRepository userGeofenceEventRepository;

    public GeofenceService(VehicleGeofenceEventRepository vehicleGeofenceEventRepository, UserGeofenceEventRepository userGeofenceEventRepository) {
        this.vehicleGeofenceEventRepository = vehicleGeofenceEventRepository;
        this.userGeofenceEventRepository = userGeofenceEventRepository;
    }

    public VehicleGeofenceEvent addVehicleGeofenceEvent(VehicleGeofenceEvent event) {
        return vehicleGeofenceEventRepository.insert(event);
    }
    public List<VehicleGeofenceEvent> getVehicleGeofenceEventsForVehicle(String vehicleId) {
        return vehicleGeofenceEventRepository.findByVehicleId(vehicleId);
    }
    public List<UserGeofenceEvent> getUserGeofenceEventsForLandmark(String landmarkId) {
        return userGeofenceEventRepository.findByLandmarkId(landmarkId);
    }
    //
    public UserGeofenceEvent addUserGeofenceEvent(UserGeofenceEvent event) {
        return userGeofenceEventRepository.insert(event);
    }
    public List<UserGeofenceEvent> getUserGeofenceEventsForUser(String userId) {
        return userGeofenceEventRepository.findByUserId(userId);
    }
    public List<VehicleGeofenceEvent> getVehicleGeofenceEventsForLandmark(String landmarkId) {
        return vehicleGeofenceEventRepository.findByLandmarkId(landmarkId);
    }
}
