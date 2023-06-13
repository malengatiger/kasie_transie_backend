package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.Association;
import com.boha.kasietransie.data.dto.RegistrationBag;
import com.boha.kasietransie.data.dto.User;
import com.boha.kasietransie.data.repos.AssociationRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import util.Constants;
import util.E;

import java.util.UUID;
import java.util.logging.Logger;

@Service
public class AssociationService {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.getLogger(AssociationService.class.getSimpleName());

    private static final String MM = "\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E ";

    private final AssociationRepository associationRepository;
    private final UserService userService;


    public AssociationService(AssociationRepository associationRepository, UserService userService) {
        this.associationRepository = associationRepository;
        this.userService = userService;
        logger.info(MM +" VehicleService constructed ");

    }

    public RegistrationBag registerAssociation(Association association) throws Exception {
        logger.info(E.LEAF+E.LEAF+ " registerAssociation starting ........... ");
                Association ass = associationRepository.insert(association);
        logger.info(E.LEAF+E.LEAF+ " Association: " + ass.getAssociationName() + " added to MongoDB database");

        User u = new User();
        u.setAssociationId(ass.getAssociationId());
        u.setFirstName(ass.getAdminUserFirstName());
        u.setLastName(ass.getAdminUserLastName());
        u.setCellphone(ass.getAdminCellphone());
        u.setPassword(UUID.randomUUID().toString());
        u.setEmail(ass.getAdminEmail());
        u.setUserId(ass.getUserId());
        u.setAssociationName(ass.getAssociationName());
        u.setCountryId(ass.getCountryId());
        u.setCountryName(ass.getCountryName());
        u.setDateRegistered(DateTime.now().toDateTimeISO().toString());
        u.setUserType(Constants.ASSOCIATION_OFFICIAL);

        User user = userService.createUser(u);
        RegistrationBag bag = new RegistrationBag(ass, user);
        logger.info(E.LEAF+E.LEAF+ " Association Admin Official: " + u.getName() + " registered OK");
        return bag;
    }
}
