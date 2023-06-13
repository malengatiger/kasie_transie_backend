package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.DispatchRecord;
import com.boha.kasietransie.data.repos.DispatchRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {
    private final DispatchRecordRepository dispatchRecordRepository;

    public DispatchService(DispatchRecordRepository dispatchRecordRepository) {
        this.dispatchRecordRepository = dispatchRecordRepository;
    }

    public DispatchRecord addDispatchRecord(DispatchRecord dispatchRecord) {
        return dispatchRecordRepository.insert(dispatchRecord);
    }
    public List<DispatchRecord> getLandmarkDispatchRecords(String landmarkId) {
        return dispatchRecordRepository.findByLandmarkId(landmarkId);
    }
    public List<DispatchRecord> getVehicleDispatchRecords(String vehicleId) {
        return dispatchRecordRepository.findByVehicleId(vehicleId);
    }
    public List<DispatchRecord> getAssociationDispatchRecords(String associationId) {
        return dispatchRecordRepository.findByAssociationId(associationId);
    }
}
