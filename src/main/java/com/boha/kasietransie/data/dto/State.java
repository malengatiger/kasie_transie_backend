package com.boha.kasietransie.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("State")
public class State {
    @Id
    private String _id;
    private String stateId;
    private String countryId;
    private String name;
    private String countryName;
    @JsonProperty("state_code")
    private String stateCode;
    private double latitude;
    private double longitude;
}