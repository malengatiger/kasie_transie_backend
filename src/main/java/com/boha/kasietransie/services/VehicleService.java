package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.Association;
import com.boha.kasietransie.data.dto.Vehicle;
import com.boha.kasietransie.data.dto.VehicleHeartbeat;
import com.boha.kasietransie.data.repos.AssociationRepository;
import com.boha.kasietransie.data.repos.VehicleHeartbeatRepository;
import com.boha.kasietransie.data.repos.VehicleRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import util.E;
import util.FileToVehicles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleHeartbeatRepository vehicleHeartbeatRepository;
    private final AssociationRepository associationRepository;
    private final ResourceLoader resourceLoader;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.getLogger(VehicleService.class.getSimpleName());

    private static final String MM = "\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26";

    public VehicleService(VehicleRepository vehicleRepository,
                          VehicleHeartbeatRepository vehicleHeartbeatRepository,
                          AssociationRepository associationRepository,
                          ResourceLoader resourceLoader) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleHeartbeatRepository = vehicleHeartbeatRepository;
        this.associationRepository = associationRepository;
        this.resourceLoader = resourceLoader;
        logger.info(MM + " VehicleService constructed ");

    }

    public Vehicle addVehicle(Vehicle vehicle) {
        Vehicle v = vehicleRepository.insert(vehicle);
        logger.info("Vehicle has been added to database");
        return v;
    }

    public List<Vehicle> getAssociationVehicles(String associationId) {
        return vehicleRepository.findByAssociationId(associationId);
    }

    public List<Vehicle> importVehiclesFromJSON(File file, String associationId) throws IOException {
        List<Association> asses = associationRepository.findByAssociationId(associationId);
        List<Vehicle> vehicles = new ArrayList<>();

        if (!asses.isEmpty()) {
            List<Vehicle> vehiclesFromJSONFile = FileToVehicles.getVehiclesFromJSONFile(file);
            vehicles = processVehiclesFromFile(associationId, asses, vehiclesFromJSONFile);

        }
        return vehicles;
    }

    private List<Vehicle> processVehiclesFromFile(String associationId,
                                                  List<Association> asses,
                                                  List<Vehicle> vehiclesFromJSONFile) {
        List<Vehicle> vehicles;
        for (Vehicle vehicle : vehiclesFromJSONFile) {
            vehicle.setAssociationId(associationId);
            vehicle.setAssociationName(asses.get(0).getAssociationName());
            vehicle.setCreated(DateTime.now().toDateTimeISO().toString());
            vehicle.setCountryId(asses.get(0).getCountryId());
            vehicle.setVehicleId(UUID.randomUUID().toString());
            vehicle.setActive(0);
        }
        vehicles = vehicleRepository.insert(vehiclesFromJSONFile);
        logger.info("Vehicles imported from file: " + vehicles.size());
        return vehicles;
    }

    public List<Vehicle> importVehiclesFromCSV(File file, String associationId) throws IOException {
        List<Association> asses = associationRepository.findByAssociationId(associationId);
        List<Vehicle> vehicles = new ArrayList<>();
        List<Vehicle> vehiclesFromCSVFile = FileToVehicles.getVehiclesFromCSVFile(file);

        if (!asses.isEmpty()) {
            vehicles = processVehiclesFromFile(associationId, asses, vehiclesFromCSVFile);

        }
        return vehicles;
    }

    public List<Vehicle> generateFakeVehicles(String associationId, int number) {
        List<Vehicle> list = new ArrayList<>();
        List<Association> asses = associationRepository.findByAssociationId(associationId);
        if (asses.isEmpty()) {
            logger.info(E.RED_DOT + " Association not found!");
            return new ArrayList<>();
        }
        for (int i = 0; i < number; i++) {
            Vehicle mVehicle = getBaseVehicle(associationId,
                    asses.get(0).getAssociationName());
            list.add(mVehicle);
        }
        List<Vehicle> mList = vehicleRepository.insert(list);
        logger.info(E.FROG + E.FROG + E.FROG + " vehicles added to database: " + mList.size());
        for (Vehicle v : mList) {
            logger.info(E.BLUE_DOT + E.BLUE_DOT + " VEHICLE inside Mongo: " + gson.toJson(v));

        }
        return mList;
    }

    public List<Vehicle> generateFakeVehiclesFromFile(String associationId) throws IOException {
        logger.info(E.BLUE_DOT + " Getting fake vehicles from file ... ");
        Resource resource = resourceLoader.getResource("classpath:vehicles.json");
        File file = resource.getFile();

        List<Association> asses = associationRepository.findByAssociationId(associationId);
        if (asses.isEmpty()) {
            logger.info(E.RED_DOT + E.RED_DOT + " Association not found: "
                    + associationId + ", cannot generate fake vehicles");
            return new ArrayList<>();
        }
        //
        List<Vehicle> vehiclesFromJSONFile = FileToVehicles.getVehiclesFromJSONFile(file);
        //fill up objects
        for (Vehicle v : vehiclesFromJSONFile) {
            v.setAssociationId(asses.get(0).getAssociationId());
            v.setAssociationName(asses.get(0).getAssociationName());
            v.setCountryId(asses.get(0).getCountryId());
            v.setVehicleId(UUID.randomUUID().toString());
            v.setCountryId(asses.get(0).getCountryId());
            v.setVehicleId(UUID.randomUUID().toString());
            v.setCreated(DateTime.now().toString());
        }

        List<Vehicle> mList = vehicleRepository.insert(vehiclesFromJSONFile);
        logger.info(E.LEAF + E.LEAF + E.LEAF + " Fake vehicles added to database " + mList.size());
        for (Vehicle vehicle : mList) {
            logger.info(E.LEAF + E.LEAF + " VEHICLE inside MongoDB's p..y: " + gson.toJson(vehicle));
        }

        return mList;
    }

    private Vehicle getBaseVehicle(String associationId, String associationName) {
        Vehicle v  = new Vehicle();

        v.setAssociationId(associationId);
        v.setActive(0);
        v.setMake("Toyota");
        v.setModel("Quantum");
        v.setOwnerId("Not a Real Id");
        v.setOwnerName("Mr. Transportation III");
        v.setYear("2018");
        v.setCreated(DateTime.now().toDateTimeISO().toString());
        v.setAssociationName(associationName);
        v.setVehicleId(UUID.randomUUID().toString());
        v.setPassengerCapacity(16);
        v.setVehicleReg(getVehicleReg());

        return v;

    }

    String getVehicleReg() {
        Random rand = new Random(System.currentTimeMillis());
        String[] alpha = {"V", "B", "F", "D", "K", "G", "H", "R", "P", "Y", "T", "W", "Q", "M", "N", "X", "F", "V", "B", "Z"};
        StringBuilder sb = new StringBuilder();
        sb.append(alpha[rand.nextInt(alpha.length - 1)]);
        sb.append(alpha[rand.nextInt(alpha.length - 1)]);
        sb.append(alpha[rand.nextInt(alpha.length - 1)]);
        sb.append(" ");
        sb.append(rand.nextInt(9));
        sb.append(rand.nextInt(9));
        sb.append(" ");
        sb.append("GP");

        return sb.toString();
    }
}
