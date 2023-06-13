package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.City;
import com.boha.kasietransie.data.dto.Country;
import com.boha.kasietransie.data.repos.CityRepository;
import com.boha.kasietransie.data.repos.CountryRepository;
import com.boha.kasietransie.data.repos.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.stereotype.Service;
import util.E;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.near;

@Service
public class CityService {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.getLogger(MongoService.class.getSimpleName());
    private static final String XX = E.COFFEE + E.COFFEE + E.COFFEE;

    final UserRepository userRepository;
    final MongoClient mongoClient;
    final CityRepository cityRepository;
    final CountryRepository countryRepository;

    @Value("${databaseName}")
    private String databaseName;

    public CityService(UserRepository userRepository,
                       MongoClient mongoClient,
                       CityRepository cityRepository,
                       CountryRepository countryRepository) {

        this.userRepository = userRepository;
        this.mongoClient = mongoClient;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    public List<City> getCountryCities(String countryId) {
        return cityRepository.findByCountryId(countryId);
    }
    public List<Country> getCountries() {
        return countryRepository.findAll();
    }
    public List<City> findCitiesByLocation(double latitude, double longitude, double radiusInKM) {
        org.springframework.data.geo.Point point = new org.springframework.data.geo.Point(longitude, latitude);
        Distance distance = new Distance(radiusInKM, Metrics.KILOMETERS);
        GeoResults<City> cities = cityRepository.findByPositionNear(point, distance);

        List<City> mList = new ArrayList<>();
        if (cities == null) {
            return mList;
        }

        for (GeoResult<City> city : cities) {
            mList.add(city.getContent());
        }
        logger.info(E.LEAF + E.LEAF + E.LEAF + E.LEAF + E.LEAF + " Cities found around location with radius: "
                + radiusInKM + " km; found " + mList.size() + " cities");
        for (City city : mList) {
            logger.info(E.LEAF + E.LEAF + " city: " + city.getName() + ", " + E.RED_APPLE + city.getStateName() + " - " + city.getCountryName());
        }
        return mList;
    }
    public List<City> getCitiesNear(double latitude, double longitude,
                                    double minDistanceInMetres,
                                    double maxDistanceInMetres) {

        MongoDatabase mongoDb = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> cityCollection = mongoDb.getCollection(City.class.getSimpleName());
        Point myPoint = new Point(new Position(longitude, latitude));
        Bson query = near("position", myPoint,
                maxDistanceInMetres, minDistanceInMetres);
        final List<City> cities = new ArrayList<>();

        cityCollection.find(query)
                .forEach(doc -> {
                    String json = doc.toJson();
                    City city = gson.fromJson(json, City.class);
                    cities.add(city);
                });

        logger.info(E.PINK+E.PINK+E.PINK+"" + cities.size()
                + " cities found with min: " + minDistanceInMetres
                + " max: " + maxDistanceInMetres);

        Collections.sort(cities);
        HashMap<String, City> map = filter(cities);
        List<City> filteredCities = map.values().stream().toList();
        int count = 0;
        for (City place : filteredCities) {
            count++;
            logger.info(E.LEAF+E.LEAF+" City: #" + count + " " + E.RED_APPLE + " " + place.getName()
                    + ", " + place.getProvince());
        }


        return filteredCities;

    }
    private static HashMap<String, City> filter(List<City> cities) {
        HashMap<String,City> map = new HashMap<>();
        for (City city : cities) {
            if (!map.containsKey(city.getName())) {
                map.put(city.getName(), city);
            }
        }
        return map;
    }

}
