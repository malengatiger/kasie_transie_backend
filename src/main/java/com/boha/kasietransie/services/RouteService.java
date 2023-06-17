package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.Route;
import com.boha.kasietransie.data.dto.RoutePoint;
import com.boha.kasietransie.data.repos.RoutePointRepository;
import com.boha.kasietransie.data.repos.RouteRepository;
import com.github.davidmoten.geo.GeoHash;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    final RoutePointRepository routePointRepository;

    public RouteService(RouteRepository routeRepository, RoutePointRepository routePointRepository) {
        this.routeRepository = routeRepository;
        this.routePointRepository = routePointRepository;
    }

    public Route addRoute(Route route) {
        return routeRepository.insert(route);
    }

    public int addRoutePoints(List<RoutePoint> routePoints) {
        for (RoutePoint rp : routePoints) {
            String geoHash = GeoHash.encodeHash(rp.getPosition().getLatitude(),
                    rp.getPosition().getLongitude());
            rp.setGeoHash(geoHash);
        }
        List<RoutePoint> list = routePointRepository.insert(routePoints);
        return list.size();
    }
    public List<Route> getAssociationRoutes(String associationId) {
        return routeRepository.findByAssociationId(associationId);
    }
    public List<RoutePoint> getRoutePoints(String routeId) {
        return routePointRepository.findByRouteId(routeId);
    }
    public List<Route> findRoutesByLocation(double latitude, double longitude, double radiusInKM) {
        List<Route> routes = new ArrayList<>();
        org.springframework.data.geo.Point point =
                new org.springframework.data.geo.Point(longitude, latitude);
        Distance distance = new Distance(radiusInKM, Metrics.KILOMETERS);
        GeoResults<RoutePoint> routePointGeoResults = routePointRepository.findByPositionNear(point, distance);

        List<RoutePoint> mList = new ArrayList<>();
        for (GeoResult<RoutePoint> routePointGeoResult : routePointGeoResults) {
            mList.add(routePointGeoResult.getContent());
        }
        HashMap<String, String> map = new HashMap<>();
        for (RoutePoint routePoint : mList) {
            if (!map.containsKey(routePoint.getRouteId())) {
                map.put(routePoint.getRouteId(),routePoint.getRouteId());
            }
        }
        for (String value : map.values()) {
            List<Route> list = routeRepository.findByRouteId(value);
            routes.addAll(list);
        }

        return routes;
    }
}
