package com.boha.kasietransie.data.dto;

import lombok.Data;

@Data
public class CalculatedDistance {
    String routeName;
    String routeID;
    String fromLandmark;
    String toLandmark;
    String fromLandmarkID;
    String toLandmarkID;
    double distanceInMetres;
    double distanceFromStart;
    int fromRoutePointIndex;
    int toRoutePointIndex;
}
