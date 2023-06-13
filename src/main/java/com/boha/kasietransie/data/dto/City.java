package com.boha.kasietransie.data.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "City")
public class City implements Comparable<City> {

    private String _partitionKey;
    @Id
    private String _id;
    private String name;
    private String cityId;
    private String country;
    private String countryId;
    private String stateId;
    private String stateName;
    private String countryName;
    private String province;
    private Position position;
    private double latitude;
    private double longitude;


    @Override
    public int compareTo(City o) {
        return this.name.compareTo(o.name);
    }
}

