package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.VehicleHeartbeat;
import com.boha.kasietransie.data.repos.HeartbeatRepository;
import com.github.davidmoten.geo.GeoHash;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeartbeatService {


    private final HeartbeatRepository heartbeatRepository;
    private final MongoTemplate mongoTemplate;

    public HeartbeatService(HeartbeatRepository heartbeatRepository, MongoTemplate mongoTemplate) {
        this.heartbeatRepository = heartbeatRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public int addVehicleHeartbeat(VehicleHeartbeat heartbeat) {
        String geoHash = GeoHash.encodeHash(heartbeat.getPosition().getLatitude(),
                heartbeat.getPosition().getLongitude());
        heartbeat.setGeoHash(geoHash);
        heartbeatRepository.insert(heartbeat);
       return 0;
    }
    public List<VehicleHeartbeat> getAssociationVehicleHeartbeats(String associationId, int cutoffHours) {
        DateTime dt = DateTime.now().minusHours(cutoffHours);
        String startDate = dt.toDateTimeISO().toString();
        Criteria c = Criteria.where("associationId").is(associationId)
                .and("date").gte(startDate);
        Query query = new Query(c);
        return mongoTemplate.find(query, VehicleHeartbeat.class);
    }
    public List<VehicleHeartbeat> getVehicleHeartbeats(String vehicleId, int cutoffHours) {
        DateTime dt = DateTime.now().minusHours(cutoffHours);
        String startDate = dt.toDateTimeISO().toString();
        Criteria c = Criteria.where("vehicleId").is(vehicleId)
                .and("date").gte(startDate);
        Query query = new Query(c);
        return mongoTemplate.find(query, VehicleHeartbeat.class);
    }
    public List<VehicleHeartbeat> getOwnerVehicleHeartbeats(String userId, int cutoffHours) {
        DateTime dt = DateTime.now().minusHours(cutoffHours);
        String startDate = dt.toDateTimeISO().toString();
        Criteria c = Criteria.where("userId").is(userId)
                .and("date").gte(startDate);
        Query query = new Query(c);
        return mongoTemplate.find(query, VehicleHeartbeat.class);
    }
}