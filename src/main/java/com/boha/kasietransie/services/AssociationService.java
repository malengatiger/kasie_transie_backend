package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.*;
import com.boha.kasietransie.data.repos.AssociationRepository;
import com.boha.kasietransie.data.repos.CityRepository;
import com.boha.kasietransie.data.repos.CountryRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import util.Constants;
import util.E;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class AssociationService {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.getLogger(AssociationService.class.getSimpleName());

    private static final String MM = "\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E ";

    private final AssociationRepository associationRepository;
    private final UserService userService;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;


    public AssociationService(AssociationRepository associationRepository, UserService userService, CountryRepository countryRepository, CityRepository cityRepository) {
        this.associationRepository = associationRepository;
        this.userService = userService;
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
        logger.info(MM + " AssociationService constructed ");

    }

    public RegistrationBag registerAssociation(Association association) throws Exception {
        logger.info(E.LEAF + E.LEAF + " registerAssociation starting ........... ");
        association.setDateRegistered(DateTime.now().toDateTimeISO().toString());

        User u = new User();
        u.setAssociationId(association.getAssociationId());
        u.setFirstName(association.getAdminUserFirstName());
        u.setLastName(association.getAdminUserLastName());
        u.setCellphone(association.getAdminCellphone());
        u.setPassword(UUID.randomUUID().toString());
        u.setEmail(association.getAdminEmail());
        u.setAssociationName(association.getAssociationName());
        u.setCountryId(association.getCountryId());
        u.setCountryName(association.getCountryName());
        u.setDateRegistered(DateTime.now().toDateTimeISO().toString());
        u.setUserType(Constants.ASSOCIATION_OFFICIAL);

        Association ass = null;
        try {
            User user = userService.createUser(u);
            association.setUserId(user.getUserId());
            u.setUserId(user.getUserId());
            ass = associationRepository.insert(association);
            logger.info(E.LEAF + E.LEAF + " Association: " + ass.getAssociationName() + " added to MongoDB database");

            RegistrationBag bag = new RegistrationBag(ass, user);
            logger.info(E.LEAF + E.LEAF + " Association Admin Official: " + u.getName() + " registered OK");
            return bag;

        } catch (Exception e) {
            try {
                if (u.getUserId() == null) {
                    FirebaseAuth.getInstance().deleteUser(u.getUserId());
                    logger.info(E.RED_DOT + "Successfully deleted user.");
                }
                if (ass != null) {
                    associationRepository.delete(ass);
                }
            } catch (Exception ex) {
                throw new Exception("Firebase or MongoDB failed to create user or " +
                        "association; registration broke down! : " + ex.getMessage());
            }

        }
        throw new Exception("Firebase or MongoDB failed to create user or " +
                "association; registration broke down; like, crashed and burned!!");
    }

    public RegistrationBag generateFakeAssociation(String associationName,
                                                   String email,
                                                   String testCellphoneNumber,
                                                   String firstName,
                                                   String lastName) throws Exception {
        Association ass = new Association();
        List<Country> cs = countryRepository.findByName("South Africa");
        List<City> cities = cityRepository.findByName("Johannesburg");
        RegistrationBag bag = null;
        if (!cs.isEmpty()) {
            ass.setAssociationName(associationName);
            ass.setAssociationId(UUID.randomUUID().toString());
            ass.setCountryId(cs.get(0).getCountryId());
            ass.setCountryName(cs.get(0).getName());
            if (!cities.isEmpty()) {
                ass.setCityId(cities.get(0).getCityId());
                ass.setCityName(cities.get(0).getName());
            }
            ass.setAdminEmail(email);
            ass.setAdminCellphone("+" + testCellphoneNumber);
            ass.setActive(0);
            ass.setAdminUserFirstName(firstName);
            ass.setAdminUserLastName(lastName);
            ass.setDateRegistered(DateTime.now().toDateTimeISO().toString());
            final double lat = -26.195246;
            final double lng = 28.034088;
            List<Double> coords = new ArrayList<>();
            coords.add(lng);
            coords.add(lat);
            ass.setPosition(new Position(
                    "Point", coords, lat, lng
            ));
        }
        if (ass.getAssociationName() != null) {
            bag = registerAssociation(ass);
            logger.info(E.RED_APPLE + E.RED_APPLE + " Fake association on the books! " + E.LEAF);
            logger.info(E.RED_APPLE + E.RED_APPLE + " Fake association and admin user: " + gson.toJson(bag));
        } else {
            logger.severe(E.RED_DOT + " fake Association crashed and burned! " + E.RED_DOT);
        }

        return bag;
    }
}
