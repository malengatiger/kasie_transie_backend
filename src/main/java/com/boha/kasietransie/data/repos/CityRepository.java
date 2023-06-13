package com.boha.kasietransie.data.repos;

import com.boha.kasietransie.data.dto.City;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CityRepository extends MongoRepository<City, String> {
}
