package com.boha.kasietransie.controllers;

import com.boha.kasietransie.data.RouteBag;
import com.boha.kasietransie.data.dto.*;
import com.boha.kasietransie.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import util.CustomErrorResponse;
import util.E;

import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ListController {
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(ListController.class);

    private final MongoService mongoService;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final AssociationService associationService;
    private final UserGeofenceService userGeofenceService;
    private final DispatchService dispatchService;
    private final CityService cityService;
    private final RouteService routeService;
    private final HeartbeatService heartbeatService;
    private final LandmarkService landmarkService;

    @GetMapping("/getUserById")
    public ResponseEntity<Object> getUserById(@RequestParam String userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getUserById failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationById")
    public ResponseEntity<Object> getAssociationById(@RequestParam String associationId) {
        try {
            Association ass = associationService.getAssociationById(associationId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationById failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationSettings")
    public ResponseEntity<Object> getAssociationSettingsModels(@RequestParam String associationId) {
        try {
            List<SettingsModel> ass = associationService
                    .getAssociationSettingsModels(associationId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationSettingsModels failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationUsers")
    public ResponseEntity<Object> getAssociationUsers(@RequestParam String associationId) {
        try {
            List<User> ass = userService
                    .getAssociationUsers(associationId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationRoutes failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationRoutes")
    public ResponseEntity<Object> getAssociationRoutes(@RequestParam String associationId) {
        try {
            List<Route> ass = routeService
                    .getAssociationRoutes(associationId);
            logger.info(E.DOG + E.DOG + " Association Routes found: " + ass.size());
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationRoutes failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationVehicles")
    public ResponseEntity<Object> getAssociationVehicles(@RequestParam String associationId) {
        try {
            List<Vehicle> ass = vehicleService
                    .getAssociationVehicles(associationId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationVehicles failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociations")
    public ResponseEntity<Object> getAssociations() {
        try {
            List<Association> ass = associationService
                    .getAssociations();
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociations failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationVehicleHeartbeats")
    public ResponseEntity<Object> getAssociationVehicleHeartbeats(@RequestParam String associationId,
                                                                  @RequestParam int cutoffHours) {
        try {
            List<VehicleHeartbeat> ass = heartbeatService
                    .getAssociationVehicleHeartbeats(associationId, cutoffHours);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationVehicleHeartbeats failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getVehicleHeartbeats")
    public ResponseEntity<Object> getVehicleHeartbeats(@RequestParam String associationId,
                                                       @RequestParam int cutoffHours) {
        try {
            List<VehicleHeartbeat> ass = heartbeatService
                    .getVehicleHeartbeats(associationId, cutoffHours);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getVehicleHeartbeats failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getOwnerVehicleHeartbeats")
    public ResponseEntity<Object> getOwnerVehicleHeartbeats(@RequestParam String associationId,
                                                            @RequestParam int cutoffHours) {
        try {
            List<VehicleHeartbeat> ass = heartbeatService
                    .getOwnerVehicleHeartbeats(associationId, cutoffHours);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getOwnerVehicleHeartbeats failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationAppErrors")
    public ResponseEntity<Object> getAssociationAppErrors(@RequestParam String associationId) {
        try {
            List<AppError> ass = associationService
                    .getAssociationAppErrors(associationId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationAppErrors failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getRoutePoints")
    public ResponseEntity<Object> getRoutePoints(@RequestParam String routeId) {
        try {
            List<RoutePoint> ass = routeService
                    .getRoutePoints(routeId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getRoutePoints failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationRoutePoints")
    public ResponseEntity<Object> getAssociationRoutePoints(@RequestParam String associationId) {
        try {
            List<RoutePoint> ass = routeService
                    .getAssociationRoutePoints(associationId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationRoutePoints failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationRouteCities")
    public ResponseEntity<Object> getAssociationRouteCities(@RequestParam String associationId) {
        try {
            List<RouteCity> ass = routeService
                    .getAssociationRouteCities(associationId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationRouteCities failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getAssociationRouteLandmarks")
    public ResponseEntity<Object> getAssociationRouteLandmarks(@RequestParam String associationId) {
        try {
            List<RouteLandmark> ass = routeService
                    .getAssociationRouteLandmarks(associationId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getAssociationRouteLandmarks failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getRouteCities")
    public ResponseEntity<Object> getRouteCities(@RequestParam String routeId) {
        try {
            List<RouteCity> ass = routeService
                    .getRouteCities(routeId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getRouteCities failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getCalculatedDistances")
    public ResponseEntity<Object> getCalculatedDistances(@RequestParam String routeId) {
        try {
            List<CalculatedDistance> ass = routeService
                    .getCalculatedDistances(routeId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getCalculatedDistances failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/refreshRoute")
    public ResponseEntity<Object> refreshRoute(@RequestParam String routeId) {
        try {
            RouteBag bag = routeService
                    .refreshRoute(routeId);
            return ResponseEntity.ok(bag);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "refreshRoute failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getCountryStates")
    public ResponseEntity<Object> getCountryStates(@RequestParam String countryId) {
        try {
            List<State> ass = cityService
                    .getCountryStates(countryId);
            return ResponseEntity.ok(ass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getCountryStates failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/findCitiesByLocation")
    public ResponseEntity<Object> findCitiesByLocation(@RequestParam double latitude,
                                                       @RequestParam double longitude,
                                                       @RequestParam int limit,
                                                       @RequestParam double radiusInKM) {
        try {
            List<City> cities = cityService.findCitiesByLocation(
                    latitude, longitude, radiusInKM, limit);
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(500,
                            "findCitiesByLocation failed: "
                                    + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/findRoutesByLocation")
    public ResponseEntity<Object> findRoutesByLocation(@RequestParam double latitude,
                                                       @RequestParam double longitude,
                                                       @RequestParam double radiusInKM) {
        try {
            List<Route> r = routeService.findRoutesByLocation(latitude, longitude, radiusInKM);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "findRoutesByLocation failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/findRouteLandmarksByLocation")
    public ResponseEntity<Object> findRouteLandmarksByLocation(@RequestParam String associationId, @RequestParam double latitude,
                                                               @RequestParam double longitude,
                                                               @RequestParam double radiusInKM) {
        try {
            List<RouteLandmark> r = routeService.findRouteLandmarksByLocation(
                    associationId,
                    latitude, longitude, radiusInKM);

            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "findRouteLandmarksByLocation failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/findLandmarksByLocation")
    public ResponseEntity<Object> findLandmarksByLocation(

            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radiusInKM) {
        try {
            List<Landmark> r = landmarkService.findLandmarksByLocation(latitude, longitude, radiusInKM);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "findLandmarksByLocation failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getRouteLandmarks")
    public ResponseEntity<Object> getRouteLandmarks(@RequestParam String routeId) {
        try {
            List<RouteLandmark> r = routeService.getRouteLandmarks(routeId);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getRouteLandmarks failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/findAssociationRoutesByLocation")
    public ResponseEntity<Object> findAssociationRoutesByLocation(@RequestParam String associationId, @RequestParam double latitude,
                                                                  @RequestParam double longitude,
                                                                  @RequestParam double radiusInKM) {
        try {
            List<Route> r = routeService.findAssociationRoutesByLocation(associationId, latitude, longitude, radiusInKM);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "findAssociationRoutesByLocation failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getCountryCities")
    public ResponseEntity<Object> getCountryCities(@RequestParam String countryId) {
        try {
            List<City> cities = cityService.getCountryCities(countryId);
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getCountryCities failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }

    @GetMapping("/getCountries")
    public ResponseEntity<Object> getCountries() {
        try {
            List<Country> cities = cityService.getCountries();
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "getCountries failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }
}
