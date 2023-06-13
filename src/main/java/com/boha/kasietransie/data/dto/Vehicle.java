package com.boha.kasietransie.data.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Vehicle")
public class Vehicle {
    String ownerId;
    String vehicleId;
    String associationId;
    String ownerName;
    String associationName;
    String created;
    String updated;
    String dateInstalled;
    String vehicleReg;
    String model;
    String make;
    int passengerCapacity;
    int active;
}
