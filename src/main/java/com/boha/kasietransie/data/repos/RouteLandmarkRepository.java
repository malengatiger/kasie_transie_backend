package com.boha.kasietransie.data.repos;

import com.boha.kasietransie.data.dto.Route;
import com.boha.kasietransie.data.dto.RouteLandmark;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RouteLandmarkRepository extends MongoRepository<RouteLandmark, String> {
    List<RouteLandmark> findByRouteId(String routeId);
    List<RouteLandmark> findByAssociationId(String associationId);

    List<RouteLandmark> findByAssociationIdOrderByCreatedAsc(String associationId);

    List<RouteLandmark> findByRouteIdOrderByCreatedAsc(String routeId);

}
