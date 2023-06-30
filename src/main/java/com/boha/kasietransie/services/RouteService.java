package com.boha.kasietransie.services;

import com.boha.kasietransie.data.CalculatedDistanceList;
import com.boha.kasietransie.data.RouteBag;
import com.boha.kasietransie.data.dto.*;
import com.boha.kasietransie.data.repos.*;
import com.github.davidmoten.geo.GeoHash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
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

    final AssociationRepository associationRepository;

    final CalculatedDistanceRepository calculatedDistanceRepository;
    final MongoTemplate mongoTemplate;

    public RouteService(RouteRepository routeRepository,
                        RoutePointRepository routePointRepository,
                        RouteLandmarkRepository routeLandmarkRepository,
                        RouteCityRepository routeCityRepository, AssociationRepository associationRepository, CalculatedDistanceRepository calculatedDistanceRepository, MongoTemplate mongoTemplate) {
        this.routeRepository = routeRepository;
        this.routePointRepository = routePointRepository;
        this.routeLandmarkRepository = routeLandmarkRepository;
        this.routeCityRepository = routeCityRepository;
        this.associationRepository = associationRepository;
        this.calculatedDistanceRepository = calculatedDistanceRepository;
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

            logger.info("Route has been refreshed for user. " + E.LEAF + E.LEAF + E.LEAF);
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

    public List<CalculatedDistance> addCalculatedDistances(CalculatedDistanceList list) {

        List<CalculatedDistance> calculatedDistances = list.getCalculatedDistances();
        logger.info(XX + " Bulk insert of " + calculatedDistances.size() + " calculatedDistances");
        if (!calculatedDistances.isEmpty()) {
            String routeId = calculatedDistances.get(0).getRouteId();
            calculatedDistanceRepository.deleteByRouteId(routeId);
            logger.info(E.BLUE_DOT + " calculatedDistances deleted: " + calculatedDistances.size());
        }
        int inserted = 0;
        try {
            Instant start = Instant.now();
            mongoTemplate.setWriteConcern(WriteConcern.W1.withJournal(true));

            BulkOperations bulkInsertion = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,
                    CalculatedDistance.class);

            for (CalculatedDistance routePoint : calculatedDistances) bulkInsertion.insert(routePoint);

            BulkWriteResult bulkWriteResult = bulkInsertion.execute();

            inserted = bulkWriteResult.getInsertedCount();
            int delta = calculatedDistances.size() - inserted;
            logger.info(XX + " Number of failed inserts : " + E.RED_DOT + delta);
            logger.info(XX + " Bulk insert of " + inserted
                    + " calculatedDistances completed in " + Duration.between(start, Instant.now()).toMillis()
                    + " milliseconds");
        } catch (Exception e) {
            logger.info(E.RED_DOT + E.RED_DOT + E.RED_DOT + " Probable dup key insert");
            logger.severe(e.getMessage());
        }
        return calculatedDistances;
    }

    public RouteLandmark addRouteLandmark(RouteLandmark routeLandmark) {
        return routeLandmarkRepository.insert(routeLandmark);
    }

    public RouteLandmark updateRouteLandmark(RouteLandmark routeLandmark) {
        return routeLandmarkRepository.save(routeLandmark);
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

    public List<CalculatedDistance> getCalculatedDistances(String routeId) {
        return calculatedDistanceRepository.findByRouteId(routeId);
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

    public List<RoutePoint> findRoutePointsByLocation(double latitude, double longitude, double radiusInKM) {

        org.springframework.data.geo.Point point =
                new org.springframework.data.geo.Point(longitude, latitude);
        Distance distance = new Distance(radiusInKM, Metrics.KILOMETERS);
        GeoResults<RoutePoint> routePointGeoResults = routePointRepository.findByPositionNear(point, distance);

        List<RoutePoint> points = new ArrayList<>();
        for (GeoResult<RoutePoint> routePointGeoResult : routePointGeoResults) {
            points.add(routePointGeoResult.getContent());
        }
        return points;
    }

    public int fixRoutePoints() {
        List<RoutePoint> points = routePointRepository.findAll();
        for (RoutePoint point : points) {
            point.setRoutePointId(UUID.randomUUID().toString());
        }
        routePointRepository.saveAll(points);
        logger.info(E.LEAF + E.LEAF + " Route points fixed");
        return points.size();
    }

    public List<Route> findAssociationRoutesByLocation(String associationId,
                                                       double latitude,
                                                       double longitude,
                                                       double radiusInKM) {


        logger.info(E.COOL_MAN + E.COOL_MAN
                + " findAssociationRoutesByLocation: radius: " + radiusInKM);
        logger.info(E.COOL_MAN + E.COOL_MAN
                + " findAssociationRoutesByLocation: lat: " + latitude + " lng: " + longitude);
        org.springframework.data.geo.Point point =
                new org.springframework.data.geo.Point(longitude, latitude);

        Distance distance = new Distance(radiusInKM, Metrics.KILOMETERS);
        GeoResults<RoutePoint> geoResults = routePointRepository.findByPositionNear(point, distance);

        logger.info(E.COOL_MAN + E.COOL_MAN
                + " findAssociationRoutesByLocation, points: " + geoResults.getContent().size());

        HashMap<String, RoutePoint> map = new HashMap<>();
        int count = 0;
        for (GeoResult<RoutePoint> routePoint : geoResults) {
            map.put(routePoint.getContent().getRouteId(), routePoint.getContent());
//            if (count > 1000) {
//            logger.info(E.BASKET_BALL + " what is this distance thing? "
//                    + routePoint.getDistance().getValue() + " metres?" + routePoint.getDistance() + " index: " + routePoint.getContent().getIndex());
//            }
//            count++;
        }
        List<Route> routes = new ArrayList<>();
        List<RoutePoint> mPoints = map.values().stream().toList();
        for (RoutePoint mPoint : mPoints) {
            List<Route> rs = routeRepository.findByRouteId(mPoint.getRouteId());
            routes.addAll(rs);
        }

        logger.info(E.COOL_MAN + E.COOL_MAN
                + " findAssociationRoutesByLocation, routes: " + routes.size());

        List<Route> filteredRoutes = new ArrayList<>();
        for (Route route : routes) {
            if (route.getAssociationId().equalsIgnoreCase(associationId)) {
                filteredRoutes.add(route);
            }
        }
        logger.info(E.COOL_MAN + E.COOL_MAN
                + " findAssociationRoutesByLocation, filteredRoutes: " + filteredRoutes.size());
        for (Route filteredRoute : filteredRoutes) {
            logger.info(E.BASKET_BALL + "Nearest Route: " + filteredRoute.getName()
                    + E.HAND1 + " color: " + filteredRoute.getColor());
        }

        return filteredRoutes;
    }
    public List<RouteLandmark> getAssociationRouteLandmarks(String associationId) {
        return routeLandmarkRepository.findByAssociationId(associationId);
    }
    public List<RoutePoint> getAssociationRoutePoints(String associationId) {
        return routePointRepository.findByAssociationId(associationId);
    }
    public List<RouteCity> getAssociationRouteCities(String associationId) {
        return routeCityRepository.findByAssociationId(associationId);
    }
    private List<Route> getRoutesFromRoutePoints(GeoResults<RoutePoint> routePointGeoResults) {
        List<Route> routes = new ArrayList<>();
        List<RoutePoint> list = new ArrayList<>();
        for (GeoResult<RoutePoint> departureGeoResult : routePointGeoResults) {
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

    public List<RouteLandmark> updateAssociationRouteLandmarks(String associationId) {

        List<RouteLandmark> routeLandmarks = new ArrayList<>();
        Association association = null;
        List<Association> associations = associationRepository.findByAssociationId(associationId);
        if (!associations.isEmpty()) {
            association = associations.get(0);
        }
        if (association != null) {
            logger.info("\n\n" + E.BASKET_BALL + E.BASKET_BALL + E.BASKET_BALL
                    + " ASSOCIATION ROUTE LANDMARK UPDATE " + association.getAssociationName() + " " + E.RED_DOT);

        }
        List<Route> routes = routeRepository.findByAssociationId(associationId);
        for (Route route : routes) {
            routeLandmarks.addAll(updateRouteLandmarks(route.getRouteId()));
        }
        if (association != null) {
            logger.info(E.BASKET_BALL + E.BASKET_BALL + E.BASKET_BALL +
                    "association: " + association.getAssociationName() + E.RED_APPLE
                    + " - routes updated! " + routes.size() + " " + E.LEAF + E.LEAF + E.LEAF);
        }

        return routeLandmarks;
    }

    public List<RouteLandmark> updateRouteLandmarks(String routeId) {
        Instant start = Instant.now();
        Route route = null;
        List<Route> routes = routeRepository.findByRouteId(routeId);
        if (!routes.isEmpty()) {
            route = routes.get(0);
        }
        if (route == null) {
            return new ArrayList<>();
        }
        List<RouteLandmark> routeLandmarks = routeLandmarkRepository.findByRouteId(routeId);
        int count = 1;
        for (RouteLandmark routeLandmark : routeLandmarks) {
            logger.info(E.BLUE_DOT + " #" + count + " landmark: " + routeLandmark.getLandmarkName());
            count++;
        }

        try {
            List<RoutePoint> routePoints = routePointRepository.findByRouteId(routeId);
            logger.info(E.BASKET_BALL + E.BASKET_BALL + E.BASKET_BALL + E.BASKET_BALL +
                    "ROUTE LANDMARK UPDATE: route: " + route.getName()
                    + " routeLandmarks: " + routeLandmarks.size() + " routePoints: " + routePoints.size());

            for (RouteLandmark routeLandmark : routeLandmarks) {
                //reset routeLandmark
                routeLandmark.setRoutePointId(null);
                routeLandmark.setRoutePointIndex(-1);
                routeLandmarkRepository.save(routeLandmark);
                List<RoutePoint> points = findRoutePointsByLocation(routeLandmark.getPosition().getCoordinates().get(1),
                        routeLandmark.getPosition().getCoordinates().get(0), 0.5);
                if (!points.isEmpty()) {
                    RoutePoint point = points.get(0);
                    //update routeLandmark
                    routeLandmark.setRoutePointId(point.getRoutePointId());
                    routeLandmark.setRoutePointIndex(point.getIndex());
                    routeLandmarkRepository.save(routeLandmark);
                    logger.info(E.BASKET_BALL + "routePoint found and routeLandmark updated  " + E.LEAF
                            + " index: " + point.getIndex() + " routePointId: " + point.getRoutePointId() + E.RED_APPLE
                            + " landmark: " + routeLandmark.getLandmarkName() + " route: " + routeLandmark.getRouteName());
                    break;
                }
            }
            //
            routeLandmarks = routeLandmarkRepository.findByRouteId(routeId);
            logger.info(E.LEAF + E.LEAF + E.LEAF + E.LEAF +
                    " Updated route: " + route.getName()
                    + " has " + routeLandmarks.size() + " routeLandmarks");

            routeLandmarks = putRouteLandmarksInOrder(routeId);
            //print results
            count = 1;
            for (RouteLandmark routeLandmark : routeLandmarks) {
                logger.info(E.LEAF + E.LEAF + " Updated routeLandmark: #" + count + " " + gson.toJson(routeLandmark));
                count++;
            }

            logger.info(E.BASKET_BALL + E.BASKET_BALL + E.BASKET_BALL
                    + " Route update complete for: " + route.getName() + " with " + routeLandmarks.size() + " routeLandmarks; elapsed time: "
                    + Duration.between(start, Instant.now()).toSeconds() + " seconds \n\n");
            fixRoutePoints(routeId, route.getName());
        } catch (Exception e) {
            logger.info(E.RED_DOT + E.RED_DOT + " we fell over, Boss! " + e.getMessage());
            e.printStackTrace();
        }

        return routeLandmarks;
    }

    public List<RouteLandmark> putRouteLandmarksInOrder(String routeId) {
        List<RouteLandmark> routeLandmarks = routeLandmarkRepository.findByRouteIdOrderByCreatedAsc(routeId);
        List<RouteLandmark> routeFilteredLandmarks = new ArrayList<>();
        int index = 0;
        for (RouteLandmark routeLandmark : routeLandmarks) {
            routeLandmark.setIndex(index);
            RouteLandmark routeLandmark1 = routeLandmarkRepository.save(routeLandmark);
            routeFilteredLandmarks.add(routeLandmark1);
            logger.info(E.BLUE_DOT + " Ordered by date: index: " + index + E.RED_APPLE
                    + " landmark: " + routeLandmark1.getLandmarkName());
            index++;

        }
        return routeFilteredLandmarks;
    }

    private void fixRoutePoints(String routeId, String routeName) {
        Instant start = Instant.now();
        List<RoutePoint> points = routePointRepository.findByRouteIdOrderByCreatedAsc(routeId);
        logger.info("fixRoutePoints .... Do we have any points? ..... " + points.size());
        if (points.isEmpty()) {
            logger.info(E.RED_DOT+" fixRoutePoints: ... this route has no routePoints: " + routeName);
            return;
        }
        int index = 0;
        for (RoutePoint point : points) {
            point.setRouteName(routeName);
            point.setIndex(index);
            index++;
        }
        routePointRepository.deleteAll();

        BulkOperations bulkInsertion = mongoTemplate.bulkOps(
                BulkOperations.BulkMode.UNORDERED, RoutePoint.class);
        bulkInsertion.insert(points);
        BulkWriteResult bulkWriteResult = bulkInsertion.execute();

        long inserted = bulkWriteResult.getInsertedCount();
        logger.info(E.CLOVER + E.CLOVER + " Bulk insert of RoutePoints added: "
                + inserted + " documents; elapsed time: "
                + Duration.between(start, Instant.now()).toSeconds() + " seconds");

        long failed = points.size() - inserted;
        if (failed > 0) {
            logger.info(E.RED_DOT + E.RED_DOT + " Countries failed during bulk insert: " + failed + "; possible duplicates " + E.RED_DOT);
        }
    }
}
