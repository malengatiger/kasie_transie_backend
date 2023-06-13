package com.boha.kasietransie.data.repos;

import com.boha.kasietransie.data.dto.Country;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CountryRepository extends MongoRepository<Country, String> {
}
