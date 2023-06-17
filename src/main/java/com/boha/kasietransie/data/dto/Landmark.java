package com.boha.kasietransie.data.dto;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import util.E;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Data
@Document(collection = "Landmark")
public class Landmark {
    private String _partitionKey;
    @Id
    private String _id;
    String landmarkId;
    String associationId;
    String created;
    double latitude;
    double longitude;
    double distance;
    String landmarkName;
    String geoHash;
    List<RouteInfo> routeDetails = new ArrayList<>();
    List<City> cities = new ArrayList<>();
    Position position;

    private static final Logger logger = Logger.getLogger(Vehicle.class.getSimpleName());
    private static final String XX = E.COFFEE + E.COFFEE + E.COFFEE;

    public static void createIndex(MongoDatabase db) {
        MongoCollection<org.bson.Document> dbCollection =
                db.getCollection(Landmark.class.getSimpleName());

        dbCollection.createIndex(
                Indexes.ascending("associationId", "landmarkName"));
        dbCollection.createIndex(
                Indexes.ascending("landmarkId"));

        dbCollection.createIndex(
                Indexes.geo2dsphere("position"));

        dbCollection.createIndex(
                Indexes.ascending("associationId", "associationName"),
                new IndexOptions().unique(true));

        logger.info(XX + "DispatchRecord indexes done");
    }
}
