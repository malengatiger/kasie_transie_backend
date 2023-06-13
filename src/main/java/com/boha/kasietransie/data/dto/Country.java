package com.boha.kasietransie.data.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "Country")
public class Country {
    private String _partitionKey;
    @Id
    private String _id;
    private String countryId;
    private String name;
    private String countryCode;
    private Position position;


}
