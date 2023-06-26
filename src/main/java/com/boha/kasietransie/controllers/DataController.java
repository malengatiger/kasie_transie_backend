package com.boha.kasietransie.controllers;

import com.boha.kasietransie.data.dto.*;
import com.boha.kasietransie.services.*;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import util.CustomErrorResponse;
import util.E;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class DataController {
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    private final MongoService mongoService;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final AssociationService associationService;
    private final UserGeofenceService userGeofenceService;
    private final DispatchService dispatchService;
    private final LandmarkService landmarkService;
    private final RouteService routeService;
    private final HeartbeatService heartbeatService;
    private final LocationRequestService locationRequestService;
    final CityService cityService;
    final GeoHashFixer geoHashFixer;
    final MessagingService messagingService;

    @PostMapping("/createUser")
    public ResponseEntity<Object> createUser(@RequestBody User user) {

        try {
            User v = userService.createUser(user);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "createUser failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    /*

     */
    @PostMapping("/addCity")
    public ResponseEntity<Object> addCity(@RequestBody City city) {

        try {
            City v = cityService.addCity(city);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addCity failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @GetMapping("/updateRouteColor")
    public ResponseEntity<Object> updateRouteColor(@RequestParam String routeId, @RequestParam String color) {

        try {
            Route v = routeService.updateRouteColor(routeId,color);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "updateRouteColor failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @PostMapping("/addVehicle")
    public ResponseEntity<Object> addVehicle(@RequestBody Vehicle vehicle) {

        try {
            Vehicle v = vehicleService.addVehicle(vehicle);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addVehicle failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @PostMapping("/addBasicLandmark")
    public ResponseEntity<Object> addLandmark(@RequestBody Landmark landmark)  {

        try {
            logger.info(E.RED_APPLE + " DataController: adding landmark: " + G.toJson(landmark));
            Landmark v = landmarkService.addBasicLandmark(landmark);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addLandmark failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @PostMapping("/addRouteLandmark")
    public ResponseEntity<Object> addRouteLandmark(@RequestBody RouteLandmark landmark)  {

        try {
            logger.info(E.RED_APPLE + " DataController: adding route landmark: " + G.toJson(landmark));
            RouteLandmark v = routeService.addRouteLandmark(landmark);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addRouteLandmark failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @PostMapping("/addRouteCity")
    public ResponseEntity<Object> addRouteCity(@RequestBody RouteCity landmark)  {

        try {
            RouteCity v = routeService.addRouteCity(landmark);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addRouteCity failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @PostMapping("/addSettingsModel")
    public ResponseEntity<Object> addSettingsModel(@RequestBody SettingsModel model)  {

        try {
            SettingsModel v = associationService.addSettingsModel(model);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addSettingsModel failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @PostMapping("/addRoute")
    public ResponseEntity<Object> addRoute(@RequestBody Route route)  {

        try {
            Route v = routeService.addRoute(route);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addRoute failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @PostMapping("/addRoutePoints")
    public ResponseEntity<Object> addRoutePoints(@RequestBody RoutePointList routePoints)  {

        try {
            int v = routeService.addRoutePoints(routePoints.getRoutePoints());
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addRoutePoints failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @PostMapping("/addDispatchRecord")
    public ResponseEntity<Object> addDispatchRecord(@RequestBody DispatchRecord dispatchRecord)  {

        try {
            DispatchRecord v = dispatchService.addDispatchRecord(dispatchRecord);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addVehicle failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @PostMapping("/addVehicleHeartbeat")
    public ResponseEntity<Object> addVehicleHeartbeat(@RequestBody VehicleHeartbeat vehicleHeartbeat)  {

        try {
            int v = heartbeatService.addVehicleHeartbeat(vehicleHeartbeat);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addVehicleHeartbeat failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @PostMapping("/addVehicleArrival")
    public ResponseEntity<Object> addVehicleArrival(@RequestBody VehicleArrival vehicleArrival)  {

        try {
            VehicleArrival v = dispatchService.addVehicleArrival(vehicleArrival);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addVehicleArrival failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @PostMapping("/addLocationRequest")
    public ResponseEntity<Object> addLocationRequest(@RequestBody LocationRequest locationRequest)  {

        try {
            LocationRequest v = locationRequestService.addLocationRequest(locationRequest);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addLocationRequest failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }
    @PostMapping("/addLocationResponse")
    public ResponseEntity<Object> addLocationResponse(@RequestBody LocationResponse locationResponse) {

        try {
            LocationResponse v = locationRequestService.addLocationResponse(locationResponse);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addLocationResponse failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }
    @PostMapping("/addUserGeofenceEvent")
    public ResponseEntity<Object> addUserGeofenceEvent(@RequestBody UserGeofenceEvent userGeofenceEvent) {

        try {
            UserGeofenceEvent v = userGeofenceService.addUserGeofenceEvent(userGeofenceEvent);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addUserGeofenceEvent failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @PostMapping("/addVehicleDeparture")
    public ResponseEntity<Object> addVehicleDeparture(@RequestBody VehicleDeparture vehicleDeparture) {

        try {
            VehicleDeparture v = dispatchService.addVehicleDeparture(vehicleDeparture);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addVehicleDeparture failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }
    }
    @PostMapping("/addAppError")
    public ResponseEntity<Object> addAppError(@RequestBody AppError appError) {

        try {
            AppError v = associationService.addAppError(appError);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addAppError failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }


    @PostMapping("/registerAssociation")
    public ResponseEntity<Object> registerAssociation(@RequestBody Association association)  {

        try {
            return ResponseEntity.ok(associationService.registerAssociation(association));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "registerAssociation failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/sendRouteUpdateMessage")
    public ResponseEntity<Object> sendRouteUpdateMessage(
                                                          @RequestParam String associationId,
                                                          @RequestParam String routeId)  {

        try {
            return ResponseEntity.ok(messagingService.sendRouteUpdateMessage(
                    associationId,routeId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "sendRouteUpdateMessage failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @GetMapping("/sendVehicleUpdateMessage")
    public ResponseEntity<Object> sendVehicleUpdateMessage(
            @RequestParam String associationId,
            @RequestParam String vehicleId)  {

        try {
            return ResponseEntity.ok(messagingService.sendVehicleUpdateMessage(
                    associationId,vehicleId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "sendVehicleUpdateMessage failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/generateFakeAssociation")
    public ResponseEntity<Object> generateFakeAssociation(@RequestParam String testCellphoneNumber,
                                                          @RequestParam String firstName,
                                                          @RequestParam String lastName,
                                                          @RequestParam String associationName,
                                                          @RequestParam String email)  {

        try {
            return ResponseEntity.ok(associationService.generateFakeAssociation(
                    associationName,email,testCellphoneNumber,firstName,lastName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "generateFakeAssociation failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @GetMapping("/generateFakeVehiclesFromFile")
    public ResponseEntity<Object> generateFakeVehiclesFromFile(@RequestParam String associationId)  {

        try {
            return ResponseEntity.ok(vehicleService.generateFakeVehiclesFromFile(
                    associationId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "generateFakeVehiclesFromFile failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @GetMapping("/generateFakeVehicles")
    public ResponseEntity<Object> generateFakeVehicles(@RequestParam String associationId,
                                                       @RequestParam int number) {

        try {
            return ResponseEntity.ok(vehicleService.generateFakeVehicles(
                    associationId,number));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "generateFakeVehicles failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/deleteRoutePoint")
    public ResponseEntity<Object> deleteRoutePoint(@RequestParam String routePointId)  {

        try {
            return ResponseEntity.ok(routeService.deleteRoutePoint(routePointId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "deleteRoutePoint failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }
    @GetMapping("/deleteByLandmarkId")
    public ResponseEntity<Object> deleteByLandmarkId(@RequestParam String landmarkId)  {

        try {
            return ResponseEntity.ok(landmarkService.deleteLandmark(landmarkId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "deleteByLandmarkId failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @PostMapping("uploadUserFile")
    public ResponseEntity<Object> uploadUserFile(
            @RequestParam String associationId,
            @RequestPart MultipartFile document) throws IOException {

        List<User> users = new ArrayList<>();
        String doc = document.getOriginalFilename();
        if (doc == null) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "Problem with user file ",
                            new DateTime().toDateTimeISO().toString()));
        }
        File file = new File(doc);
        Files.write(document.getBytes(), file);
        logger.info("\uD83C\uDF3C\uD83C\uDF3C we have a file: " + file.getName());
        if (file.getName().contains(".csv")) {
            logger.info("\uD83C\uDF3C\uD83C\uDF3C csv file to process: " + file.getName());
            try {
                users = userService.importUsersFromCSV(file, associationId);
                if (users.isEmpty()) {
                    logger.info("\uD83C\uDF3C\uD83C\uDF3C no users created ... wtfObject ");
                    return ResponseEntity.badRequest().body(
                            new CustomErrorResponse(400,
                                    "Failed to create users; no users in file or file is not .json or .csv ",
                                    new DateTime().toDateTimeISO().toString()));
                }

                return ResponseEntity.ok(users);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(
                        new CustomErrorResponse(400,
                                "Failed to create users: " + e.getMessage(),
                                new DateTime().toDateTimeISO().toString()));
            }
        }
        if (file.getName().contains(".json")) {
            logger.info("\uD83C\uDF3C\uD83C\uDF3C json file to process: " + file.getName());
            try {
                users = userService.importUsersFromJSON(file, associationId);
                if (users.isEmpty()) {
                    logger.info("\uD83C\uDF3C\uD83C\uDF3C no users created ... wtfObject ");
                    return ResponseEntity.badRequest().body(
                            new CustomErrorResponse(400,
                                    "Failed to create users; no users in file or file is not .json or .csv ",
                                    new DateTime().toDateTimeISO().toString()));
                }

                return ResponseEntity.ok(users);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(
                        new CustomErrorResponse(400,
                                "Failed to create users: " + e.getMessage(),
                                new DateTime().toDateTimeISO().toString()));
            }
        }
        if (file.exists()) {
            Path path = Paths.get(file.getAbsolutePath());
            java.nio.file.Files.delete(path);
        }

        return ResponseEntity.badRequest().body(
                new CustomErrorResponse(400,
                        "Failed to create users; no users in file or file is not .json or .csv ",
                        new DateTime().toDateTimeISO().toString()));
    }

    @PostMapping("uploadVehicleFile")
    public ResponseEntity<Object> uploadVehicleFile(
            @RequestParam String associationId,
            @RequestPart MultipartFile document) throws IOException {

        List<Vehicle> vehicles;
        String doc = document.getOriginalFilename();
        if (doc == null) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "Problem with user file ",
                            new DateTime().toDateTimeISO().toString()));
        }
        File file = new File(doc);
        Files.write(document.getBytes(), file);
        logger.info("\uD83C\uDF3C\uD83C\uDF3C we have a file: " + file.getName());
        if (file.getName().contains(".csv")) {
            logger.info("\uD83C\uDF3C\uD83C\uDF3C csv file to process: " + file.getName());
            try {
                vehicles = vehicleService.importVehiclesFromCSV(file, associationId);
                if (vehicles.isEmpty()) {
                    logger.info("\uD83C\uDF3C\uD83C\uDF3C no vehicles created ... wtfObject ");
                    return ResponseEntity.badRequest().body(
                            new CustomErrorResponse(400,
                                    "Failed to create vehicles; no vehicles in file or file is not .json or .csv ",
                                    new DateTime().toDateTimeISO().toString()));
                }

                return ResponseEntity.ok(vehicles);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(
                        new CustomErrorResponse(400,
                                "Failed to create vehicles: " + e.getMessage(),
                                new DateTime().toDateTimeISO().toString()));
            }
        }
        if (file.getName().contains(".json")) {
            logger.info("\uD83C\uDF3C\uD83C\uDF3C json file to process: " + file.getName());
            try {
                vehicles = vehicleService.importVehiclesFromJSON(file, associationId);
                if (vehicles.isEmpty()) {
                    logger.info("\uD83C\uDF3C\uD83C\uDF3C no vehicles created ... wtfObject ");
                    return ResponseEntity.badRequest().body(
                            new CustomErrorResponse(400,
                                    "Failed to create vehicles; no vehicles in file or file is not .json or .csv ",
                                    new DateTime().toDateTimeISO().toString()));
                }

                return ResponseEntity.ok(vehicles);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(
                        new CustomErrorResponse(400,
                                "Failed to create vehicles: " + e.getMessage(),
                                new DateTime().toDateTimeISO().toString()));
            }
        }
        if (file.exists()) {
            Path path = Paths.get(file.getAbsolutePath());
            java.nio.file.Files.delete(path);

        }

        return ResponseEntity.badRequest().body(
                new CustomErrorResponse(400,
                        "Failed to create vehicles; no vehicles in file or file is not .json or .csv ",
                        new DateTime().toDateTimeISO().toString()));
    }


    @GetMapping("/addCountriesStatesCitiesToDB")
    public ResponseEntity<Object> addCountriesStatesCitiesToDB()  {

        try {
            return ResponseEntity.ok(mongoService.addCountriesStatesCitiesToDB());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addCountriesStatesCitiesToDB failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/addGeoHashes")
    public ResponseEntity<Object> addGeoHashes()  {

        try {
            return ResponseEntity.ok(geoHashFixer.addGeoHashes());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addGeoHashes failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/addSouthAfricanCitiesToDB")
    public ResponseEntity<Object> addSouthAfricanCitiesToDB()  {

        try {
            return ResponseEntity.ok(mongoService.addSouthAfricanCitiesToDB());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addSouthAfricanCitiesToDB failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/checkDatabaseTotals")
    public ResponseEntity<Object> checkDatabaseTotals() {

        try {
            return ResponseEntity.ok(mongoService.checkDatabaseTotals());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "checkDatabaseTotals failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/fixRoutePoints")
    public ResponseEntity<Object> fixRoutePoints() {

        try {
            return ResponseEntity.ok(routeService.fixRoutePoints());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "checkDatabaseTotals failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/")

    public String ping() {
        return "Ola BRICS!! - KasieTransie waiting for you at the Rank! \uD83C\uDF50\uD83C\uDF50\uD83C\uDF50\uD83C\uDF50";
    }

}
