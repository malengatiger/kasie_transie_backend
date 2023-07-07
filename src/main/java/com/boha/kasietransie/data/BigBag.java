package com.boha.kasietransie.data;

import com.boha.kasietransie.data.dto.DispatchRecord;
import com.boha.kasietransie.data.dto.VehicleArrival;
import com.boha.kasietransie.data.dto.VehicleDeparture;
import com.boha.kasietransie.data.dto.VehicleHeartbeat;
import lombok.Data;

import java.util.List;

@Data
public class BigBag {
    List<VehicleArrival> vehicleArrivals;
    List<DispatchRecord> dispatchRecords;
    List<VehicleHeartbeat> vehicleHeartbeats;
    List<VehicleDeparture> vehicleDepartures;
}
