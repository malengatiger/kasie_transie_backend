package com.boha.kasietransie.data.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "User")
public class User {
    @Id
    private String _id;
    String userType;
    String userId;
    String firstName;
    String lastName;
    String gender;
    String countryId;
    String associationId;
    String associationName;
    String fcmToken;
    String email;
    String cellphone;
    String password;
    String countryName;
    String dateRegistered;


    public String getName() {
        return firstName + " " + lastName;
    }
}
