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
@Document(collection = "DispatchRecord")
public class DispatchRecord {
    private String _partitionKey;
    @Id
    private String _id;
    String dispatchRecordId;
    String landmarkId;
    String marshalId;
    int passengers;
    String ownerId;
    String created;
    Position position;
    String landmarkName;
    String marshalName;
    String routeName;
    String routeId;
    String vehicleId;
    String vehicleArrivalId;
    String vehicleReg;
    String associationD;
    String associationName;
    boolean dispatched;

    private static final Logger logger = Logger.getLogger(Vehicle.class.getSimpleName());
    private static final String XX = E.COFFEE + E.COFFEE + E.COFFEE;

    public static void createIndex(MongoDatabase db) {
        MongoCollection<org.bson.Document> dbCollection =
                db.getCollection(DispatchRecord.class.getSimpleName());

        dbCollection.createIndex(
                Indexes.ascending("associationId", "landmarkId", "created"));
        dbCollection.createIndex(
                Indexes.ascending( "landmarkId", "created"));

        dbCollection.createIndex(
                Indexes.ascending( "vehicleId", "created"));

        dbCollection.createIndex(
                Indexes.ascending( "marshalId", "created"));

        dbCollection.createIndex(
                Indexes.ascending( "routeId", "created"));

        dbCollection.createIndex(
                Indexes.geo2dsphere("position"));

        dbCollection.createIndex(
                Indexes.ascending("countryId","associationName"),
                new IndexOptions().unique(true));

        logger.info(XX + "DispatchRecord indexes done");
    }

}
