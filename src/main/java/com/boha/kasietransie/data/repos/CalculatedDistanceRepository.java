package com.boha.kasietransie.data.repos;

import com.boha.kasietransie.data.dto.CalculatedDistance;
import com.boha.kasietransie.data.dto.RoutePoint;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CalculatedDistanceRepository extends MongoRepository<CalculatedDistance, String> {
    List<CalculatedDistance> findByRouteId(String routeId);

    void deleteByRouteId(String routeId);
}
