package com.boha.kasietransie.data.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "RoutePoint")
public class RoutePoint {
    private String _partitionKey;
    @Id
    private String _id;
    double latitude;
    double longitude;
    double heading;
    int index;
    String created;
    String routeID;
    String landmarkID;
    String landmarkName;
    Position position;
}
