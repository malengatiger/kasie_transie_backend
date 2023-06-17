package com.boha.kasietransie.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class Position {
    String type;
    List<Double> coordinates;
    double latitude;
    double longitude;
    String geoHash;

    public Position(String type, List<Double> coordinates, double latitude, double longitude, String geoHash) {
        this.type = type;
        this.coordinates = coordinates;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geoHash = geoHash;
    }

    public Position() {
    }
}
