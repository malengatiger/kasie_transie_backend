package com.boha.kasietransie.data.repos;

import com.boha.kasietransie.data.dto.City;
import com.boha.kasietransie.data.dto.VehicleGeofenceEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VehicleGeofenceEventRepository extends MongoRepository<VehicleGeofenceEvent, String> {
    List<VehicleGeofenceEvent> findByLandmarkId(String landmarkId);
    List<VehicleGeofenceEvent> findByVehicleReg(String vehicleReg);

    List<VehicleGeofenceEvent> findByVehicleId(String vehicleId);

}
