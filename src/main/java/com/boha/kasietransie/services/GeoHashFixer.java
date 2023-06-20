package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.City;
import com.boha.kasietransie.data.dto.Country;
import com.boha.kasietransie.data.repos.CityRepository;
import com.boha.kasietransie.data.repos.CountryRepository;
import com.boha.kasietransie.data.repos.StateRepository;
import com.github.davidmoten.geo.GeoHash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class GeoHashFixer {

    final CountryRepository countryRepository;
    final StateRepository stateRepository;
    final CityRepository cityRepository;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.getLogger(GeoHashFixer.class.getSimpleName());

    private static final String MM = "\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E\uD83C\uDF4E ";


    public GeoHashFixer(CountryRepository countryRepository, StateRepository stateRepository, CityRepository cityRepository) {
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
    }

    public String addGeoHashes() {
        DateTime start = DateTime.now();
        var countries = countryRepository.findAll();
        for (Country country : countries) {
            String geoHash = GeoHash.encodeHash(country.getPosition().getLatitude(),
                    country.getPosition().getLongitude());
            country.setGeoHash(geoHash);
        }
        logger.info(MM + " start saving countries ... : " + countries.size());
        countryRepository.saveAll(countries);
        logger.info(MM + "countries saved: " + countries.size());

        var cities = fixCities();
        DateTime end = DateTime.now();
        long d = end.getMillis() - start.getMillis();
        long elapsed = d/1000;
        String sb = MM + " countries updated: " + countries.size() + "\n" +
                MM + " cities updated: " + cities + "\n" +
                MM + " elapsed time: " + elapsed + " seconds" + "\n";
        logger.info(sb);
        return sb;
    }
    public int fixCities() {
        var cities = cityRepository.findAll();
        for (City city : cities) {
            String geoHash = GeoHash.encodeHash(city.getPosition().getLatitude(),
                    city.getPosition().getLongitude());
            city.setGeoHash(geoHash);
        }
        logger.info(MM + "start saving cities in bulk ....");

        cityRepository.saveAll(cities);
        logger.info(MM + "cities saved: " + cities.size());

        return cities.size();
    }
}

