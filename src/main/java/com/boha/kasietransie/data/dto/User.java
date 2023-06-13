package com.boha.kasietransie.data.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document()
public class User {
    @Id
    private String _id;
    String userType;
    String userID;
    String firstName;
    String lastName;
    String gender;
    String countryID;
    String associationID;
    String associationName;
    String fcmToken;
    String email;
    String cellphone;
}
