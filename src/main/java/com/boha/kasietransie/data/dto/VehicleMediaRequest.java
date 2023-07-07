package com.boha.kasietransie.data.dto;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import util.E;

import java.util.logging.Logger;

@Data
@Document(collection = "VehicleMediaRequest")
public class VehicleMediaRequest {
    String userId;
    String vehicleId;
    String vehicleReg;
    String created;
    String requesterId, associationId;
    String requesterName;
    boolean addVideo;

    private static final Logger logger = Logger.getLogger(VehicleMediaRequest.class.getSimpleName());
    private static final String XX = E.COFFEE + E.COFFEE + E.COFFEE;

    public static void createIndex(MongoDatabase db) {
        MongoCollection<org.bson.Document> dbCollection =
                db.getCollection(VehicleMediaRequest.class.getSimpleName());

        dbCollection.createIndex(
                Indexes.ascending( "associationId"));
        dbCollection.createIndex(
                Indexes.ascending( "vehicleId"));

        logger.info(XX + "VehicleMediaRequest indexes done");
    }
}
