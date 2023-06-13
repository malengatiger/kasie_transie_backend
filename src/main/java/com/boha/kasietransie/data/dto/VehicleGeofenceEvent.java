package com.boha.kasietransie.data.dto;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import util.E;

import java.util.logging.Logger;

@Data
@Document(collection = "VehicleGeofenceEvent")
public class VehicleGeofenceEvent {
    private String _partitionKey;
    @Id
    private String _id;
    String landmarkId;
    String activityType;
    String action;
    String vehicleId;
    long longDate;
    String date;
    String vehicleReg;
    String make;
    String landmarkName;
    int confidence;
    double odometer;
    boolean moving;
    Position position;

    private static final Logger logger = Logger.getLogger(Vehicle.class.getSimpleName());
    private static final String XX = E.COFFEE + E.COFFEE + E.COFFEE;

    public static void createIndex(MongoDatabase db) {
        MongoCollection<org.bson.Document> dbCollection =
                db.getCollection(VehicleGeofenceEvent.class.getSimpleName());

        dbCollection.createIndex(
                Indexes.ascending( "landmarkId", "created"));

        dbCollection.createIndex(
                Indexes.ascending( "vehicleId", "created"));

        dbCollection.createIndex(
                Indexes.geo2dsphere("position"));

        logger.info(XX + "VehicleGeofenceEvent indexes done");
    }
}
