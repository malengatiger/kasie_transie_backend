package com.boha.kasietransie.data.dto;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import util.E;

import java.util.logging.Logger;

@Data
@Document(collection = "VehicleDeparture")
public class VehicleDeparture {
    private String _partitionKey;
    @Id
    private String _id;
    String vehicleDepartureId;
    String landmarkId;
    String landmarkName;
    String routeName;
    String routeId;
    String vehicleId;
    String associationId;
    String associationName;
    String vehicleReg;
    String created;
    String make;
    String model;
    Position position;

    private static final Logger logger = Logger.getLogger(Vehicle.class.getSimpleName());
    private static final String XX = E.COFFEE + E.COFFEE + E.COFFEE;

    public static void createIndex(MongoDatabase db) {
        MongoCollection<org.bson.Document> dbCollection =
                db.getCollection(VehicleDeparture.class.getSimpleName());

        dbCollection.createIndex(
                Indexes.ascending("routeId"));

        dbCollection.createIndex(
                Indexes.ascending("landmarkId"));

        dbCollection.createIndex(
                Indexes.ascending("vehicleId"));

        dbCollection.createIndex(
                Indexes.ascending("associationId"));

        dbCollection.createIndex(Indexes.geo2dsphere("position"));

        logger.info(XX + "VehicleArrival indexes done ");
    }
}
