package com.boha.kasietransie.data.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document()
public class Association {
    @Id
    private String _id;
    String associationId;
    String cityId;
    String countryId;
    String associationName;
    String phone;
    String status;
    String countryName;
    String cityName;
    String stringDate;
    int date;
    String path;
}
