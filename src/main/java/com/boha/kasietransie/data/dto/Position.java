package com.boha.kasietransie.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class Position {
    String type;
    List<Double> coordinates;
    double latitude;
    double longitude;

    public Position(String type, List<Double> coordinates, double latitude, double longitude) {
        this.type = type;
        this.coordinates = coordinates;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Position() {
    }
}
