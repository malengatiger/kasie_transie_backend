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
@Document(collection = "Route")
public class Route {
    private String _partitionKey;
    @Id
    private String _id;
    String routeID;
    String countryID;
    String countryName;
    String name;
    String routeNumber;
    String created;
    String updated;
    String color;
    String userId;
    String userName;
    int active;
    String activationDate;
    String associationID;
    String associationName;
    List<RoutePoint> rawRoutePoints;
    List<RoutePoint> routePoints;
    List<CalculatedDistance> calculatedDistances;
    List<String> landmarkIds = new ArrayList<>();
    double heading;
    int lengthInMetres;

    private static final Logger logger = Logger.getLogger(Vehicle.class.getSimpleName());
    private static final String XX = E.COFFEE + E.COFFEE + E.COFFEE;

    public static void createIndex(MongoDatabase db) {
        MongoCollection<org.bson.Document> dbCollection =
                db.getCollection(Route.class.getSimpleName());

        dbCollection.createIndex(
                Indexes.ascending("associationId", "name"));
        dbCollection.createIndex(
                Indexes.ascending( "countryId"));

        logger.info(XX + "Route indexes done");
    }
}
