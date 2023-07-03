package com.boha.kasietransie.data.repos;

import com.boha.kasietransie.data.TranslationBag;
import com.boha.kasietransie.data.dto.AppError;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TranslationBagRepository extends MongoRepository<TranslationBag, String> {
    List<TranslationBag> findByTarget(String locale);
    List<TranslationBag> findBySource(String locale);

}
