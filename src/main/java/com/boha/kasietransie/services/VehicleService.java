package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.Association;
import com.boha.kasietransie.data.dto.RoutePoint;
import com.boha.kasietransie.data.dto.User;
import com.boha.kasietransie.data.dto.Vehicle;
import com.boha.kasietransie.data.repos.AssociationRepository;
import com.boha.kasietransie.data.repos.UserRepository;
import com.boha.kasietransie.data.repos.VehicleHeartbeatRepository;
import com.boha.kasietransie.data.repos.VehicleRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mongodb.WriteConcern;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import util.E;
import util.FileToVehicles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
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
    final UserRepository userRepository;
    final CloudStorageUploaderService cloudStorageUploaderService;

    final MongoTemplate mongoTemplate;
    final MessagingService messagingService;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.getLogger(VehicleService.class.getSimpleName());

    private static final String XX = E.PRESCRIPTION + E.PRESCRIPTION + E.PRESCRIPTION;

    private static final String MM = "\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26\uD83D\uDC26";

    public VehicleService(VehicleRepository vehicleRepository,
                          VehicleHeartbeatRepository vehicleHeartbeatRepository,
                          AssociationRepository associationRepository,
                          ResourceLoader resourceLoader, UserRepository userRepository, CloudStorageUploaderService cloudStorageUploaderService, MongoTemplate mongoTemplate, MessagingService messagingService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleHeartbeatRepository = vehicleHeartbeatRepository;
        this.associationRepository = associationRepository;
        this.resourceLoader = resourceLoader;
        this.userRepository = userRepository;
        this.cloudStorageUploaderService = cloudStorageUploaderService;
        this.mongoTemplate = mongoTemplate;
        this.messagingService = messagingService;

        logger.info(MM + " VehicleService constructed and shit injected! ");

    }

    public Vehicle addVehicle(Vehicle vehicle) throws Exception {
        createVehicleQRCode(vehicle);
        Vehicle v = vehicleRepository.insert(vehicle);
        logger.info("Vehicle has been added to database");
        messagingService.sendVehicleUpdateMessage(v.getAssociationId(),v.getVehicleId());
        return v;
    }

    public List<Vehicle> getAssociationVehicles(String associationId) {
        return vehicleRepository.findByAssociationId(associationId);
    }

    public List<Vehicle> getOwnerVehicles(String userId) {
        return vehicleRepository.findByOwnerId(userId);
    }

    public int updateVehicleQRCode(Vehicle vehicle) throws Exception {
        try {
            int result = createVehicleQRCode(vehicle);
            vehicleRepository.save(vehicle);
            logger.info(E.OK + E.OK + " ... we cool with QRCode for "
                    + vehicle.getVehicleReg() + " result: " + result);

        } catch (Exception e) {
            logger.severe("Unable to create QRCode");
            return 9;
        }
        return 0;
    }

    public int createVehicleQRCode(Vehicle car) throws Exception {
        String barcodeText = gson.toJson(car);
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
                barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

        BufferedImage img = MatrixToImageWriter.toBufferedImage(bitMatrix);
        Path path = Path.of("/qr_codes/qrcode_" + System.currentTimeMillis() + ".png");
        File file = Files.createFile(path).toFile();
        ImageIO.write(img, "png", file);
        String url = cloudStorageUploaderService.uploadFile(file.getName(), file);
        car.setQrCodeUrl(url);

        boolean delete = Files.deleteIfExists(path);
        logger.info(E.LEAF + E.LEAF + E.LEAF +
                " QRCode generated, url: " + url + " for car: " + gson.toJson(car)
                + E.RED_APPLE + " - temp file deleted: " + delete);
        return 0;
    }

    public List<Vehicle> importVehiclesFromJSON(File file, String associationId) throws Exception {
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
                                                  List<Vehicle> vehiclesFromJSONFile) throws Exception {
        List<Vehicle> vehicles;
        for (Vehicle vehicle : vehiclesFromJSONFile) {
            vehicle.setAssociationId(associationId);
            vehicle.setAssociationName(asses.get(0).getAssociationName());
            vehicle.setCreated(DateTime.now().toDateTimeISO().toString());
            vehicle.setCountryId(asses.get(0).getCountryId());
            vehicle.setVehicleId(UUID.randomUUID().toString());
            vehicle.setActive(0);

            int result = createVehicleQRCode(vehicle);
            if (result == 0) {
                logger.info(E.OK + E.OK + " ... we cool with QRCode for "
                        + vehicle.getVehicleReg() + " result: " + result);
            } else {
                logger.severe(E.NOT_OK+ " Unable to create QRCode for " + vehicle.getVehicleReg());

            }


        }
        vehicles = vehicleRepository.insert(vehiclesFromJSONFile);
        logger.info("Vehicles imported from file: " + vehicles.size());
        return vehicles;
    }

    public List<Vehicle> importVehiclesFromCSV(File file, String associationId) throws Exception {
        List<Association> asses = associationRepository.findByAssociationId(associationId);
        List<Vehicle> vehicles = new ArrayList<>();
        List<Vehicle> vehiclesFromCSVFile = FileToVehicles.getVehiclesFromCSVFile(file);

        if (!asses.isEmpty()) {
            vehicles = processVehiclesFromFile(associationId, asses, vehiclesFromCSVFile);

        }
        return vehicles;
    }

    public List<Vehicle> generateFakeVehicles(String associationId, int number) throws Exception {
        List<Vehicle> list = new ArrayList<>();
        List<Association> asses = associationRepository.findByAssociationId(associationId);
        if (asses.isEmpty()) {
            logger.info(E.RED_DOT + " Association not found!");
            return new ArrayList<>();
        }
        for (int i = 0; i < number; i++) {
            Vehicle mVehicle = getBaseVehicle(associationId,
                    asses.get(0).getAssociationName());
            createVehicleQRCode(mVehicle);
            list.add(mVehicle);
        }
        List<Vehicle> mList = vehicleRepository.insert(list);
        logger.info(E.FROG + E.FROG + E.FROG + " vehicles added to database: " + mList.size());
        for (Vehicle v : mList) {
            logger.info(E.BLUE_DOT + E.BLUE_DOT + " VEHICLE inside Mongo: " + gson.toJson(v));

        }
        return mList;
    }

    public List<Vehicle> generateFakeVehiclesFromFile(String associationId) throws Exception {
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
            v.setPassengerCapacity(16);
            v.setCreated(DateTime.now().toString());
            createVehicleQRCode(v);
        }

        List<Vehicle> mList = vehicleRepository.insert(vehiclesFromJSONFile);
        logger.info(E.LEAF + E.LEAF + E.LEAF + " Fake vehicles added to database " + mList.size());
        for (Vehicle vehicle : mList) {
            logger.info(E.LEAF + E.LEAF + " VEHICLE inside MongoDB's p..y: " + gson.toJson(vehicle));
        }

        return mList;
    }

    public int changeFakeVehicleOwner(String userId) {

        List<User> users = userRepository.findByUserId(userId);
        logger.info(XX + " Number of users: " + E.RED_DOT + users.size());

        String oldOwner = "Mr. Transportation III";
        Query query = new Query();
        query.addCriteria(Criteria.where("ownerName").is(oldOwner));

         List<Vehicle> cars = mongoTemplate.find(query, Vehicle.class);
        logger.info(XX + " Number of cars: " + E.RED_DOT + cars.size());

        for (Vehicle car : cars) {
            car.setOwnerId(userId);
            car.setOwnerName(users.get(0).getName());
        }
        vehicleRepository.saveAll(cars);
        logger.info(XX + " completed of cars: " + E.RED_DOT + cars.size());

        return cars.size();


    }
    private Vehicle getBaseVehicle(String associationId, String associationName) {
        Vehicle v = new Vehicle();

        v.setAssociationId(associationId);
        v.setActive(0);
        v.setMake("Toyota");
        v.setModel("Quantum");
        v.setOwnerId("Not a Real Id");
        v.setOwnerName(getOwnerName());
        v.setYear("2018");
        v.setCreated(DateTime.now().toDateTimeISO().toString());
        v.setAssociationName(associationName);
        v.setVehicleId(UUID.randomUUID().toString());
        v.setPassengerCapacity(16);
        v.setVehicleReg(getVehicleReg());

        return v;

    }

    Random random = new Random(System.currentTimeMillis());
    private String getOwnerName() {
        String[] firstNames = new String[]{"John", "Nancy", "David", "Eric G", "Thomas A", "George", "Freddie", "Benjamin", "Thabo",
        "Thabiso", "Mmamothe", "Yvonne", "Brandy G", "Catherine", "Anthony", "Malenga", "Jimmy", "Donnie", "Samuel", "Karina"};
        String[] lastNames = new String[]{"Smith", "Baloyi", "Donaldson", "van der Merwe", "Battles", "Carpenter", "Moredi",
                "Benjamin", "Donald", "jackson", "Rostov", "Maringa", "van Wyk", "Damarin", "Phillips", "Hellenic",
                "Mofokeng", "Maluleke", "Henderson", "Marule", "Nkuna"};

        int index1 = random.nextInt(firstNames.length);
        int index2 = random.nextInt(lastNames.length);

        String name = firstNames[index1] + " " + lastNames[index2];
        int x = random.nextInt(100);
        if (x > 25) {
            return name;
        } else {
            return "Joburg South Taxi Collective Ltd.";
        }
    }

    private String getVehicleReg() {
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
