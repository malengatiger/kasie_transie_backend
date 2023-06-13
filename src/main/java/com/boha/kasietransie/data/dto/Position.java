package com.boha.kasietransie.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class Position {
    String type;
    List<Double> coordinates;
    double latitude;
    double longitude;


}
