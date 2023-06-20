package com.boha.kasietransie.data.repos;

import com.boha.kasietransie.data.dto.RoutePoint;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoutePointRepository extends MongoRepository<RoutePoint, String> {
    List<RoutePoint> findByRouteId(String countryId);

    GeoResults<RoutePoint> findByPositionNear(Point location, Distance distance);

    void deleteByRoutePointId(String routePointId);
}
