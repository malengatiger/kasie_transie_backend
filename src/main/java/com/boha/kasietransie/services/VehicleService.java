package com.boha.kasietransie.services;

import com.boha.kasietransie.KasieTransieBackendApplication;
import com.boha.kasietransie.data.dto.Association;
import com.boha.kasietransie.data.dto.User;
import com.boha.kasietransie.data.dto.Vehicle;
import com.boha.kasietransie.data.repos.AssociationRepository;
import com.boha.kasietransie.data.repos.VehicleRepository;
import com.google.api.core.ApiFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import util.FileToUsers;
import util.FileToVehicles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final AssociationRepository associationRepository;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.getLogger(VehicleService.class.getSimpleName());

    private static final String MM = "\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E ";

    public VehicleService(VehicleRepository vehicleRepository, AssociationRepository associationRepository) {
        this.vehicleRepository = vehicleRepository;
        this.associationRepository = associationRepository;
        logger.info(MM +" VehicleService constructed ");

    }

    public  List<Vehicle> importVehiclesFromJSON(File file, String associationId) throws IOException {
        List<Association> orgs = associationRepository.findByAssociationId(associationId);
        List<Vehicle> vehicles = new ArrayList<>();
        if (!orgs.isEmpty()) {
            List<Vehicle> vehiclesFromJSONFile = FileToVehicles.getVehiclesFromJSONFile(file);
            for (Vehicle vehicle : vehiclesFromJSONFile) {
                vehicle.setAssociationId(associationId);
                vehicle.setAssociationName(orgs.get(0).getAssociationName());
                vehicle.setCreated(DateTime.now().toDateTimeISO().toString());
                vehicle.setActive(0);
            }
            vehicles = vehicleRepository.insert(vehiclesFromJSONFile);
            logger.info("Vehicles imported from file: " + vehicles.size());

        }
        return vehicles;
    }
    public  List<Vehicle> importVehiclesFromCSV(File file, String associationId) throws IOException {
        List<Association> orgs = associationRepository.findByAssociationId(associationId);
        List<Vehicle> vehicles = new ArrayList<>();
        if (!orgs.isEmpty()) {
            List<Vehicle> users = FileToVehicles.getVehiclesFromCSVFile(file);
            for (Vehicle vehicle : users) {
                vehicle.setAssociationId(associationId);
                vehicle.setAssociationName(orgs.get(0).getAssociationName());
                vehicle.setCreated(DateTime.now().toDateTimeISO().toString());
                vehicle.setActive(0);
            }
            vehicles = vehicleRepository.insert(users);
            logger.info("Vehicles imported from file: " + vehicles.size());

        }
        return vehicles;
    }

}
