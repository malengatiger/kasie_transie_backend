package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.City;
import com.boha.kasietransie.data.dto.Route;
import com.boha.kasietransie.data.dto.RoutePoint;
import com.boha.kasietransie.data.repos.RoutePointRepository;
import com.boha.kasietransie.data.repos.RouteRepository;
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
        List<RoutePoint> list = routePointRepository.insert(routePoints);
        return list.size();
    }
    public List<Route> getAssociationRoutes(String associationId) {
        return routeRepository.findByAssociationId(associationId);
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
            if (!map.containsKey(routePoint.getRouteID())) {
                map.put(routePoint.getRouteID(),routePoint.getRouteID());
            }
        }
        for (String value : map.values()) {
            List<Route> list = routeRepository.findByRouteId(value);
            routes.addAll(list);
        }

        return routes;
    }
}