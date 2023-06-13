package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.DispatchRecord;
import com.boha.kasietransie.data.dto.VehicleArrival;
import com.boha.kasietransie.data.dto.VehicleDeparture;
import com.boha.kasietransie.data.repos.DispatchRecordRepository;
import com.boha.kasietransie.data.repos.VehicleArrivalRepository;
import com.boha.kasietransie.data.repos.VehicleDepartureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {
    private final DispatchRecordRepository dispatchRecordRepository;
    private final VehicleArrivalRepository vehicleArrivalRepository;
    private final VehicleDepartureRepository vehicleDepartureRepository;

    public DispatchService(DispatchRecordRepository dispatchRecordRepository, VehicleArrivalRepository vehicleArrivalRepository, VehicleDepartureRepository vehicleDepartureRepository) {
        this.dispatchRecordRepository = dispatchRecordRepository;
        this.vehicleArrivalRepository = vehicleArrivalRepository;
        this.vehicleDepartureRepository = vehicleDepartureRepository;
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
    //
    public VehicleArrival addVehicleArrival(VehicleArrival vehicleArrival) {
        return vehicleArrivalRepository.insert(vehicleArrival);
    }
    public VehicleDeparture addVehicleDeparture(VehicleDeparture vehicleDeparture) {
        return vehicleDepartureRepository.insert(vehicleDeparture);
    }
    public List<VehicleArrival> getLandmarkVehicleArrivals(String landmarkId) {
        return vehicleArrivalRepository.findByLandmarkId(landmarkId);
    }
    public List<VehicleArrival> getVehicleArrivals(String vehicleId) {
        return vehicleArrivalRepository.findByVehicleId(vehicleId);
    }
    public List<VehicleArrival> getAssociationVehicleArrivals(String associationId) {
        return vehicleArrivalRepository.findByAssociationId(associationId);
    }

    //

    public List<VehicleDeparture> getLandmarkVehicleDepartures(String landmarkId) {
        return vehicleDepartureRepository.findByLandmarkId(landmarkId);
    }
    public List<VehicleDeparture> getVehicleDeparture(String vehicleId) {
        return vehicleDepartureRepository.findByVehicleId(vehicleId);
    }
    public List<VehicleDeparture> getAssociationVehicleDepartures(String associationId) {
        return vehicleDepartureRepository.findByAssociationId(associationId);
    }

}
