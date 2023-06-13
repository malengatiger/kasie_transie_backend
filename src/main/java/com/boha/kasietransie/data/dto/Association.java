package com.boha.kasietransie.data.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Association")
public class Association {
    @Id
    private String _id;
    String associationId;
    String cityId;
    String countryId;
    String associationName;
    int active;
    String countryName;
    String cityName;
    String dateRegistered;
    Position position;
    String adminUserFirstName;
    String adminUserLastName;
    String userId;
    String adminCellphone;
    String adminEmail;



}
