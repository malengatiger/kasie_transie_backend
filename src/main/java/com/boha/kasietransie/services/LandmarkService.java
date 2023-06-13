package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.City;
import com.boha.kasietransie.data.dto.Landmark;
import com.boha.kasietransie.data.repos.LandmarkRepository;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LandmarkService {

    private final LandmarkRepository landmarkRepository;

    public LandmarkService(LandmarkRepository landmarkRepository) {
        this.landmarkRepository = landmarkRepository;
    }

    public Landmark addLandmark(Landmark landmark) {
        return landmarkRepository.insert(landmark);
    }

    public List<Landmark> getAssociationLandmarks(String associationId) {
        return landmarkRepository.findByAssociationId(associationId);
    }
    public List<Landmark> findLandmarksByLocation(double latitude,
                                                  double longitude, double radiusInKM) {
        org.springframework.data.geo.Point point =
                new org.springframework.data.geo.Point(longitude, latitude);

        Distance distance = new Distance(radiusInKM, Metrics.KILOMETERS);
        GeoResults<Landmark> landmarks = landmarkRepository.findByPositionNear(point, distance);

        List<Landmark> list = new ArrayList<>();
        for (GeoResult<Landmark> landmark : landmarks) {
            list.add(landmark.getContent());
        }

        return list;
    }
}
