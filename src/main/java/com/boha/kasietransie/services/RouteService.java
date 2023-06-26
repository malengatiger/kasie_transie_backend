package com.boha.kasietransie.services;

import com.boha.kasietransie.data.RouteBag;
import com.boha.kasietransie.data.dto.Route;
import com.boha.kasietransie.data.dto.RouteCity;
import com.boha.kasietransie.data.dto.RouteLandmark;
import com.boha.kasietransie.data.dto.RoutePoint;
import com.boha.kasietransie.data.repos.RouteCityRepository;
import com.boha.kasietransie.data.repos.RouteLandmarkRepository;
import com.boha.kasietransie.data.repos.RoutePointRepository;
import com.boha.kasietransie.data.repos.RouteRepository;
import com.github.davidmoten.geo.GeoHash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import util.E;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    final RoutePointRepository routePointRepository;
    final RouteLandmarkRepository routeLandmarkRepository;
    final RouteCityRepository routeCityRepository;
    final MongoTemplate mongoTemplate;

    public RouteService(RouteRepository routeRepository,
                        RoutePointRepository routePointRepository,
                        RouteLandmarkRepository routeLandmarkRepository,
                        RouteCityRepository routeCityRepository, MongoTemplate mongoTemplate) {
        this.routeRepository = routeRepository;
        this.routePointRepository = routePointRepository;
        this.routeLandmarkRepository = routeLandmarkRepository;
        this.routeCityRepository = routeCityRepository;
        this.mongoTemplate = mongoTemplate;
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.getLogger(RouteService.class.getSimpleName());
    private static final String XX = E.PRESCRIPTION + E.PRESCRIPTION + E.PRESCRIPTION;

    public Route addRoute(Route route) {
        return routeRepository.insert(route);
    }

    public RouteBag refreshRoute(String routeId) throws Exception {
        List<Route> list = routeRepository.findByRouteId(routeId);
        if (!list.isEmpty()) {
            Route route = list.get(0);
            List<RouteLandmark> routeLandmarks = routeLandmarkRepository.findByRouteId(routeId);
            List<RoutePoint> routePoints = routePointRepository.findByRouteId(routeId);
            List<RouteCity> routeCities = routeCityRepository.findByRouteId(routeId);
            RouteBag bag = new RouteBag();
            bag.setRoute(route);
            bag.setRoutePoints(routePoints);
            bag.setRouteLandmarks(routeLandmarks);
            bag.setRouteCities(routeCities);

            logger.info("Route has been refreshed for user. " + E.LEAF+E.LEAF+E.LEAF);
            return bag;
        }

        throw new Exception("Route not found");
    }
    public int deleteRoutePoint(String routePointId) {
        routePointRepository.deleteByRoutePointId(routePointId);
        return 0;
    }
    public Route updateRouteColor(String routeId, String color) throws Exception {
        List<Route> list = routeRepository.findByRouteId(routeId);
        if (!list.isEmpty()) {
            Route r = list.get(0);
            r.setColor(color);
            routeRepository.save(r);
            return r;
        }
        throw new Exception("color update failed");
    }

    public int addRoutePoints(List<RoutePoint> routePoints) {
        logger.info(XX + " Bulk insert of " + routePoints.size() + " routePoints");
        for (RoutePoint rp : routePoints) {
            String geoHash = GeoHash.encodeHash(rp.getPosition().getLatitude(),
                    rp.getPosition().getLongitude());
            rp.setGeoHash(geoHash);
        }
        int inserted = 0;
        try {
            Instant start = Instant.now();
            mongoTemplate.setWriteConcern(WriteConcern.W1.withJournal(true));

            BulkOperations bulkInsertion = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
                    RoutePoint.class);

            for (RoutePoint routePoint : routePoints) bulkInsertion.insert(routePoint);

            BulkWriteResult bulkWriteResult = bulkInsertion.execute();

            inserted = bulkWriteResult.getInsertedCount();
            int delta = routePoints.size() - inserted;
            logger.info(XX + " Number of failed inserts : " + E.RED_DOT + delta);
            logger.info(XX + " Bulk insert of " + inserted
                    + " routePoints completed in " + Duration.between(start, Instant.now()).toMillis()
                    + " milliseconds");
        } catch (Exception e) {
            logger.info(E.RED_DOT + E.RED_DOT + E.RED_DOT + " Probable dup key insert");
            logger.severe(e.getMessage());
        }
        return inserted;
    }

    public RouteLandmark addRouteLandmark(RouteLandmark routeLandmark) {
        return routeLandmarkRepository.insert(routeLandmark);
    }
    public RouteCity addRouteCity(RouteCity routeCity) {
        return routeCityRepository.insert(routeCity);
    }

    public List<Route> getAssociationRoutes(String associationId) {
        return routeRepository.findByAssociationId(associationId);
    }

    public List<RoutePoint> getRoutePoints(String routeId) {
        return routePointRepository.findByRouteId(routeId);
    }
    public List<RouteCity> getRouteCities(String routeId) {
        return routeCityRepository.findByRouteId(routeId);
    }
    public List<RouteLandmark> getRouteLandmarks(String routeId) {
        return routeLandmarkRepository.findByRouteId(routeId);
    }

    public List<Route> findRoutesByLocation(double latitude, double longitude, double radiusInKM) {

        org.springframework.data.geo.Point point =
                new org.springframework.data.geo.Point(longitude, latitude);
        Distance distance = new Distance(radiusInKM, Metrics.KILOMETERS);
        GeoResults<RoutePoint> routePointGeoResults = routePointRepository.findByPositionNear(point, distance);

        return getRoutesFromRoutePoints(routePointGeoResults);
    }

    public int fixRoutePoints() {
        List<RoutePoint> points = routePointRepository.findAll();
        for (RoutePoint point : points) {
            point.setRoutePointId(UUID.randomUUID().toString());
        }
        routePointRepository.saveAll(points);
        logger.info(E.LEAF+E.LEAF+" Route points fixed");
        return points.size();
    }

    public List<Route> findAssociationRoutesByLocation(String associationId,
                                                       double latitude,
                                                       double longitude,
                                                       double radiusInKM) {

        Criteria criteria = Criteria.where("associationId")
                .is(associationId);

        Query query = new Query(criteria);
        Point searchPoint = new Point(latitude, longitude);
        NearQuery nearQuery = NearQuery.near(searchPoint);
        nearQuery.spherical(true);
        nearQuery.inKilometers();
        nearQuery.maxDistance(radiusInKM);
        nearQuery.query(query);

        GeoResults<RoutePoint> routePointGeoResults = mongoTemplate.geoNear(
                nearQuery, RoutePoint.class,
                RoutePoint.class.getSimpleName(), RoutePoint.class);

        return getRoutesFromRoutePoints(routePointGeoResults);
    }

    private List<Route>  getRoutesFromRoutePoints(GeoResults<RoutePoint> arrivals) {
        List<Route> routes = new ArrayList<>();
        List<RoutePoint> list = new ArrayList<>();
        for (GeoResult<RoutePoint> departureGeoResult : arrivals) {
            list.add(departureGeoResult.getContent());
        }
        HashMap<String, String> map = new HashMap<>();
        for (RoutePoint routePoint : list) {
            if (!map.containsKey(routePoint.getRouteId())) {
                map.put(routePoint.getRouteId(), routePoint.getRouteId());
            }
        }
        for (String value : map.values()) {
            List<Route> routeList = routeRepository.findByRouteId(value);
            routes.addAll(routeList);
        }
        return routes;
    }
}
