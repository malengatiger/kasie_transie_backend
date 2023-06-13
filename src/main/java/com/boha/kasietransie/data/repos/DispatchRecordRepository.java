package com.boha.kasietransie.data.repos;

import com.boha.kasietransie.data.dto.DispatchRecord;
import com.boha.kasietransie.data.dto.UserGeofenceEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DispatchRecordRepository extends MongoRepository<DispatchRecord, String> {
    List<DispatchRecord> findByLandmarkId(String landmarkId);
    List<DispatchRecord> findByVehicleId(String vehicleId);

    List<DispatchRecord> findByMarshalId(String userId);

    List<DispatchRecord> findByAssociationId(String associationId);



}
