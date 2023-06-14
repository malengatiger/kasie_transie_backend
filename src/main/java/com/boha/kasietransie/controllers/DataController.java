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
    @PostMapping("/addLandmark")
    public ResponseEntity<Object> addLandmark(@RequestBody Landmark landmark)  {

        try {
            Landmark v = landmarkService.addLandmark(landmark);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addLandmark failed: " + e.getMessage(),
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
    public ResponseEntity<Object> addRoutePoints(@RequestBody List<RoutePoint> routePoints)  {

        try {
            int v = routeService.addRoutePoints(routePoints);
            return ResponseEntity.ok(v);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addRoutePoints failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @PostMapping("/addDispatchRecord")
    public ResponseEntity<Object> addDispatchRecord(@RequestBody DispatchRecord dispatchRecord) throws Exception {

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
    public ResponseEntity<Object> addVehicleHeartbeat(@RequestBody VehicleHeartbeat vehicleHeartbeat) throws Exception {

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
    public ResponseEntity<Object> addVehicleArrival(@RequestBody VehicleArrival vehicleArrival) throws Exception {

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
    public ResponseEntity<Object> addLocationRequest(@RequestBody LocationRequest locationRequest) throws Exception {

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
    public ResponseEntity<Object> addLocationResponse(@RequestBody LocationResponse locationResponse) throws Exception {

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
    public ResponseEntity<Object> addUserGeofenceEvent(@RequestBody UserGeofenceEvent userGeofenceEvent) throws Exception {

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
    public ResponseEntity<Object> addVehicleDeparture(@RequestBody VehicleDeparture vehicleDeparture) throws Exception {

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

    @PostMapping("/registerAssociation")
    public ResponseEntity<Object> registerAssociation(@RequestBody Association association) throws Exception {

        try {
            return ResponseEntity.ok(associationService.registerAssociation(association));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "registerAssociation failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/generateFakeAssociation")
    public ResponseEntity<Object> generateFakeAssociation(@RequestParam String testCellphoneNumber,
                                                          @RequestParam String firstName,
                                                          @RequestParam String lastName,
                                                          @RequestParam String associationName,
                                                          @RequestParam String email) throws Exception {

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
    public ResponseEntity<Object> generateFakeVehiclesFromFile(@RequestParam String associationId) throws Exception {

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
            boolean ok = file.delete();
            if (ok) {
                logger.info(E.RED_APPLE + E.RED_APPLE +
                        " user batch file deleted");
            }
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
            boolean ok = file.delete();
            if (ok) {
                logger.info(E.RED_APPLE + E.RED_APPLE +
                        " user batch file deleted");
            }
        }

        return ResponseEntity.badRequest().body(
                new CustomErrorResponse(400,
                        "Failed to create vehicles; no vehicles in file or file is not .json or .csv ",
                        new DateTime().toDateTimeISO().toString()));
    }


    @GetMapping("/addCountriesStatesCitiesToDB")
    public ResponseEntity<Object> addCountriesStatesCitiesToDB() throws Exception {

        try {
            return ResponseEntity.ok(mongoService.addCountriesStatesCitiesToDB());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new CustomErrorResponse(400,
                            "addCountriesStatesCitiesToDB failed: " + e.getMessage(),
                            new DateTime().toDateTimeISO().toString()));
        }

    }

    @GetMapping("/addSouthAfricanCitiesToDB")
    public ResponseEntity<Object> addSouthAfricanCitiesToDB() throws Exception {

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

    @GetMapping("/")

    public String ping() {
        return "Ola BRICS!! - KasieTransie waiting for you at the Rank! \uD83C\uDF50\uD83C\uDF50\uD83C\uDF50\uD83C\uDF50";
    }

}
