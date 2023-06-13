package com.boha.kasietransie.services;

import com.boha.kasietransie.data.dto.*;
import com.boha.kasietransie.data.repos.CityRepository;
import com.boha.kasietransie.data.repos.CountryRepository;
import com.boha.kasietransie.data.repos.StateRepository;
import com.boha.kasietransie.data.repos.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import util.E;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

//@RequiredArgsConstructor
@Service
public class MongoService {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = Logger.getLogger(MongoService.class.getSimpleName());
    private static final String XX = E.COFFEE + E.COFFEE + E.COFFEE;

    private final CityRepository cityRepo;
    private final MongoClient mongoClient;
    private final ResourceLoader resourceLoader;
    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    private final MongoTemplate mongoTemplate;
    private MongoDatabase db;


    public MongoService(CityRepository cityRepo,
                        MongoClient mongoClient,
                        ResourceLoader resourceLoader,
                        UserRepository userRepository,
                        CountryRepository countryRepository,
                        StateRepository stateRepository,
                        MongoTemplate mongoTemplate) {
        this.cityRepo = cityRepo;
        this.mongoClient = mongoClient;
        this.resourceLoader = resourceLoader;
        this.countryRepository = countryRepository;
        this.stateRepository = stateRepository;
        this.mongoTemplate = mongoTemplate;

        setDatabase();
    }

    private void setDatabase() {
        if (db == null) {
            db = mongoClient.getDatabase("kasie_transie_db");
            logger.info(E.RED_APPLE + " Mongo Database set up, ....... database name: " + db.getName());
        }
    }

    private void countriesBulkInsert(List<Country> countries) {
        logger.info("\n\n" + E.RAIN_DROP + E.RAIN_DROP + "Bulk insert of " + countries.size() + " countries starting ... ");
        Instant start = Instant.now();

        BulkOperations bulkInsertion = mongoTemplate.bulkOps(
                BulkOperations.BulkMode.UNORDERED, Country.class);
        bulkInsertion.insert(countries);
        BulkWriteResult bulkWriteResult = bulkInsertion.execute();

        long inserted = bulkWriteResult.getInsertedCount();
        logger.info("Bulk insert of Countries added: " + inserted + " documents; elapsed time: "
                + Duration.between(start, Instant.now()).toSeconds() + " seconds");

        long failed = countries.size() - inserted;
        if (failed > 0) {
            logger.info(E.RED_DOT + E.RED_DOT + " Countries failed during bulk insert: " + failed + "; possible duplicates " + E.RED_DOT);
        }
    }

    private void statesBulkInsert(List<State> states) {
        logger.info("\n\n" + E.FERN + E.FERN + " Bulk insert of " + states.size() + " states starting ... ");
        Instant start = Instant.now();

        BulkOperations bulkInsertion = mongoTemplate.bulkOps(
                BulkOperations.BulkMode.UNORDERED, State.class);
        bulkInsertion.insert(states);
        BulkWriteResult bulkWriteResult = bulkInsertion.execute();

        long inserted = bulkWriteResult.getInsertedCount();
        logger.info(E.FERN + E.FERN + " Bulk insert of States added: " + inserted + " documents; elapsed time: "
                + Duration.between(start, Instant.now()).toSeconds() + " seconds");

        long failed = states.size() - inserted;
        if (failed > 0) {
            logger.info(E.RED_DOT + E.RED_DOT + " States failed during bulk insert: " + failed + "; possible duplicates " + E.RED_DOT);
        }
    }

    private void citiesBulkInsert(List<City> cities) {
        logger.info("\n\n" + E.DICE + E.DICE + " Bulk insert of " + cities.size() + " cities starting ... ");
        Instant start = Instant.now();

        BulkOperations bulkInsertion = mongoTemplate.bulkOps(
                BulkOperations.BulkMode.UNORDERED, City.class);
        bulkInsertion.insert(cities);
        BulkWriteResult bulkWriteResult = bulkInsertion.execute();

        long inserted = bulkWriteResult.getInsertedCount();
        logger.info(E.DICE + E.DICE + " Bulk insert of Cities added: " + inserted + " documents; elapsed time: "
                + Duration.between(start, Instant.now()).toSeconds() + " seconds");

        long failed = cities.size() - inserted;
        if (failed > 0) {
            logger.info(E.RED_DOT + E.RED_DOT + " Cities failed during bulk insert: " + failed + "; possible duplicates " + E.RED_DOT);
        }
    }

    private CountryBag getCountriesFromFile() throws IOException {
        logger.info(XX + " Getting countries from file ... ");
        Resource resource = resourceLoader.getResource("classpath:countries.json");
        File file = resource.getFile();
        logger.info(XX + " Countries json file length: " + file.length());
        final String POINT = "Point";
        final String LATITUDE = "latitude";
        final String LONGITUDE = "longitude";

        Path p = Paths.get(file.toURI());
        String json = Files.readString(p);
        //
        List<Country> countries = new ArrayList<>();
        List<State> states = new ArrayList<>();
        List<City> cities = new ArrayList<>();

        logger.info(XX + " start adding to DB ................ ");

        try {
            JSONArray arr = new JSONArray(json);
            for (Object o : arr) {

                int countOfCitiesInCountry = 0;

                JSONObject countryJson = (JSONObject) o;
                String name = countryJson.getString("name");
                String region = countryJson.getString("region");
                String subregion = countryJson.getString("subregion");
                String iso2 = countryJson.getString("iso2");
                String iso3 = countryJson.getString("iso3");
                String phoneCode = countryJson.getString("phone_code");
                String capital = countryJson.getString("capital");
                String currency = countryJson.getString("currency");
                String currencyName = countryJson.getString("currency_name");
                String currencySymbol = countryJson.getString("currency_symbol");
                String emoji = countryJson.getString("emoji");
                double latitude = 0.0;
                double longitude = 0.0;
                if (countryJson.getString(LATITUDE) != null) {
                    latitude = Double.parseDouble(countryJson.getString(LATITUDE));
                    longitude = Double.parseDouble(countryJson.getString(LONGITUDE));
                }
                //
                Country country = new Country();
                country.setName(name);
                country.setCountryId(UUID.randomUUID().toString());
                country.setCapital(capital);
                country.setRegion(region);
                country.setSubregion(subregion);
                country.setCurrency(currency);
                country.setCurrencySymbol(currencySymbol);
                country.setCurrencyName(currencyName);
                country.setEmoji(emoji);
                country.setLatitude(latitude);
                country.setLongitude(longitude);
                country.setEmoji(emoji);
                country.setIso2(iso2);
                country.setIso3(iso3);
                country.setPhoneCode(phoneCode);
                //
                Position countryPosition = new Position();
                countryPosition.setType(POINT);
                List<Double> list = new ArrayList<>();
                list.add(longitude);
                list.add(latitude);
                countryPosition.setLatitude(latitude);
                countryPosition.setLongitude(longitude);
                countryPosition.setCoordinates(list);
                //
                country.setPosition(countryPosition);
                country.setLatitude(latitude);
                country.setLongitude(longitude);
                countries.add(country);
                logger.info(E.FERN + E.FERN + " country added to list: \uD83E\uDD6C #"
                        + countries.size() + " - "
                        + country.getName());

                //
                //process states
                JSONArray stateArr = countryJson.getJSONArray("states");
                for (Object so : stateArr) {
                    JSONObject stateJson = (JSONObject) so;
                    State state = new State();
                    state.setStateId(UUID.randomUUID().toString());
                    state.setCountryId(country.getCountryId());
                    state.setName(stateJson.getString("name"));
                    state.setStateCode(stateJson.getString("state_code"));
                    state.setCountryName(country.getName());
                    states.add(state);
                    logger.info(E.BLUE_DOT + E.BLUE_DOT + " state added to list: \uD83E\uDD6C #"
                            + states.size() + " - "
                            + state.getName());
                    //
                    //process cities

                    JSONArray cityArr = stateJson.getJSONArray("cities");
                    for (Object co : cityArr) {
                        JSONObject cityJson = (JSONObject) co;
                        City city = new City();
                        city.setCityId(UUID.randomUUID().toString());
                        city.setCountry(country.getName());
                        city.setCountryId(country.getCountryId());
                        city.setStateId(state.getStateId());
                        city.setStateName(state.getName());
                        city.setCountryName(country.getName());
                        city.setName(cityJson.getString("name"));
                        double lat;
                        double lng;
                        if (cityJson.getString(LATITUDE) != null) {
                            lat = Double.parseDouble(cityJson.getString(LATITUDE));
                            lng = Double.parseDouble(cityJson.getString(LONGITUDE));
                            city.setLatitude(lat);
                            city.setLongitude(lng);
                            Position cityPos = new Position();
                            cityPos.setType(POINT);
                            List<Double> coords = new ArrayList<>();
                            coords.add(lng);
                            coords.add(lat);
                            cityPos.setLatitude(lat);
                            cityPos.setLongitude(lng);
                            cityPos.setCoordinates(coords);
                            //
                            city.setPosition(cityPos);
                        } else {
                            logger.info(E.RED_DOT + E.RED_DOT + E.RED_DOT
                                    + " coordinates are not present " + E.RED_DOT + " city: "
                                    + city.getCountry() + " - " + city.getName());
                        }

                        cities.add(city);
                        countOfCitiesInCountry++;

                        logger.info(E.PEAR + E.PEAR + " city added to list: \uD83E\uDD6C #"
                                + cities.size() + " - "
                                + city.getStateName() + " - "
                                + city.getCountryName() + " - " + E.COOL_MAN + " "
                                + city.getName());
                    }
                }

                logger.info(E.PEAR + E.PEAR + E.PEAR + E.PEAR + " cities added to country list: \uD83E\uDD6C "
                        + countOfCitiesInCountry + " - " + country.getName());
            }

            CountryBag bag = new CountryBag();
            bag.setCountries(countries);
            bag.setStates(states);
            bag.setCities(cities);

            logger.info("\n\n Data Loading complete! " + XX + XX
                    + " Work completed " + bag);
            return bag;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private State findState(List<State> states, City city) {
        for (State state : states) {
            if (state.getName().equalsIgnoreCase(city.getStateName())) {
                return state;
            }
        }
        return null;
    }

    private List<City> getCitiesFromFile() throws IOException {
        logger.info(XX + " Getting cities from file ... current file: South Africa ");
        Resource resource = resourceLoader.getResource("classpath:cities.json");
        File file = resource.getFile();
        logger.info(XX + " Cities json file length: " + file.length());
        //get all states in SA
        String COUNTRY_ID = "9a41505e-bee3-4382-859b-090845f89367";
        List<State> states =
                stateRepository.findByCountryId(COUNTRY_ID);

        Path p = Paths.get(file.toURI());
        String json = Files.readString(p);
        List<City> cities = new ArrayList<>();

        JSONArray arr = new JSONArray(json);
        for (Object o : arr) {
            JSONObject jo = (JSONObject) o;
            String name = jo.getString("AccentCity");
            String province = jo.getString("ProvinceName");
            double latitude = jo.getDouble("Latitude");
            double longitude = jo.getDouble("Longitude");

            City city = new City();
            city.setName(name);
            city.setCityId(UUID.randomUUID().toString());
            //
            Position position = new Position();
            position.setType("Point");
            List<Double> list = new ArrayList<>();
            list.add(longitude);
            list.add(latitude);
            position.setLatitude(latitude);
            position.setLongitude(longitude);
            position.setCoordinates(list);
            //
            city.setPosition(position);
            city.setLatitude(latitude);
            city.setLongitude(longitude);

            //find state
            State state = findState(states, city);
            if (state != null) {
                if (province.equalsIgnoreCase(state.getName())) {
                    city.setStateName(state.getName());
                    city.setStateId(state.getStateId());
                    city.setCountry(state.getCountryName());
                }
            } else {
                city.setCountry("South Africa");
                city.setStateName(province);
            }
            city.setCountryId(COUNTRY_ID);
            city.setCountry("South Africa");

            cities.add(city);
        }

        logger.info(XX + " Found " + cities.size() + " cities in file\n\n");
        Collections.sort(cities);
        return cities;
    }

    public long addCitiesToDB() throws Exception {
        logger.info(XX + " ... adding cities to MongoDB ...");
        long start = System.currentTimeMillis();

        List<City> cities = getCitiesFromFile();

        long end1 = System.currentTimeMillis();
        long elapsed1 = (end1 - start);

        logger.info(E.RED_APPLE + E.RED_APPLE + E.RED_APPLE
                + " it took " + elapsed1 + " milliseconds to build list: "
                + cities.size());

        processCities(cities);

        return cities.size();
    }

    public String addCountriesStatesCitiesToDB() throws Exception {
        CountryBag countryBag = getCountriesFromFile();

        Instant start = Instant.now();

        cityRepo.deleteAll();
        stateRepository.deleteAll();
        countryRepository.deleteAll();

        logger.info(XX + " countries, states and cities: collections have been cleared ");

        assert countryBag != null;
        countriesBulkInsert(countryBag.getCountries());
        logger.info(XX + " countries added to DB: ");

        int NUMBER_STATES = 1000;
        List<State> states = countryBag.getStates();

        int numberOfStateBatches = states.size() / NUMBER_STATES;
        int rem = states.size() % NUMBER_STATES;
        if (rem > 0) {
            numberOfStateBatches++;
        }
        logger.info(XX + " State Batches  to be processed: " + numberOfStateBatches);

        for (int i = 0; i < numberOfStateBatches; i++) {
            try {
                List<State> list = states.subList(i * NUMBER_STATES, (i + 1) * NUMBER_STATES);
                statesBulkInsert(list);
                logger.info(XX + " states added to DB: " + E.RED_APPLE + " batch #" + (i + 1)
                        + " count: " + list.size() + "\n");
            } catch (Exception e) {
                logger.severe(E.RED_DOT + " State Batches complete? " + e.getMessage());
                if (e.getMessage().contains("Bulk write operation error")) {
                    logger.info(E.RED_DOT + E.RED_DOT + " Bulk write operation error, means that some docs were duplicates!");
                } else {
                    //get the rest ...
                    logger.severe(E.RED_DOT + " City Batches complete? " + e.getMessage() + " index: " + i);
                    List<State> list = states.subList(i * NUMBER_STATES, states.size() - 1);
                    statesBulkInsert(list);
                    logger.info(XX + " states added to DB: " + E.RED_APPLE + " batch #" + (i + 1)
                            + " count: " + list.size() + "\n");
                }
            }
        }
        //
        processCities(countryBag.getCities());

        long a = countryRepository.count();
        long b = stateRepository.count();
        long c = cityRepo.count();


        logger.info("\n\n" + E.CHECK + E.CHECK + E.CHECK + "Final Counts: Countries: " + a + " States: " + b + " Cities: " + c);
        logger.info(E.CHECK + E.CHECK + E.CHECK + " Data migration complete! .... elapsed time: "
                + Duration.between(start, Instant.now()).toSeconds() + " seconds or "
                + Duration.between(start, Instant.now()).toMinutes() + " minutes\n\n");

        return countryBag.toString();
    }

    private void processCities(List<City> cities) {
        int NUMBER_CITIES = 10000;
        int numberOfCityBatches = cities.size() / NUMBER_CITIES;
        int rem2 = cities.size() % NUMBER_CITIES;
        if (rem2 > 0) {
            numberOfCityBatches++;
        }
        logger.info(XX + " City Batches to be processed: " + numberOfCityBatches);


        for (int i = 0; i < numberOfCityBatches; i++) {
            try {
                List<City> list = cities.subList(i * NUMBER_CITIES, (i + 1) * NUMBER_CITIES);
                citiesBulkInsert(list);
                logger.info(XX + " cities added to DB: " + E.RED_APPLE + " batch #" + (i + 1)
                        + " count: " + list.size() + "\n");
            } catch (Exception e) {
                if (e.getMessage().contains("Bulk write operation error")) {
                    logger.info(E.RED_DOT + E.RED_DOT + " Bulk write operation error, means that some docs were duplicates!");
                } else {
                    //get the rest ...
                    logger.severe(E.RED_DOT + " City Batches complete? " + e.getMessage() + " index: " + i);

                    List<City> list = cities.subList(i * NUMBER_CITIES, cities.size() - 1);
                    citiesBulkInsert(list);
                    logger.info(XX + " cities added to DB: " + E.RED_APPLE + " batch #" + (i + 1)
                            + " count: " + list.size() + "\n");
                }
            }
        }
    }

    public List<City> getCities() {
        List<City> cities = cityRepo.findAll();
        logger.info(E.LEAF + E.LEAF + "Cities found on MongoDB: " + cities.size());
        Collections.sort(cities);
        return cities;
    }

    public void initializeIndexes() {
        logger.info(XX + " MongoService has started setup of indexes ... " + E.BELL + E.BELL);

        try {
            createCityIndexes();
            createCountryIndexes();

            logger.info(XX + " MongoService has completed setup of indexes " + E.BELL + E.BELL);

        } catch (Exception e) {
            logger.severe(E.RED_DOT + E.RED_DOT + " Index building failed: " + e.getMessage());
        }
    }

    private void createCountryIndexes() {
        //add index
        MongoCollection<Document> dbCollection = db.getCollection(Country.class.getSimpleName());

        String result2 = dbCollection.createIndex(Indexes.ascending("name"),
                new IndexOptions().unique(true));

        logger.info(E.BLUE_DOT + E.BLUE_BIRD + "Indexes created for Country: " + result2);

    }

//    private void createOrganizationIndexes() {
//        //add index
//        MongoCollection<Document> dbCollection = db.getCollection(Organization.class.getSimpleName());
//
//        String result2 = dbCollection.createIndex(Indexes.ascending("name"),
//                new IndexOptions().unique(true));
//
//        dbCollection.createIndex(Indexes.ascending("organizationId"));
//        logger.info(E.BLUE_DOT+E.BLUE_BIRD+ "Indexes created for Organization");
//
//    }
//
//    private void createSettingsIndexes() {
//        //add index
//        MongoCollection<Document> dbCollection = db.getCollection(SettingsModel.class.getSimpleName());
//
//        dbCollection.createIndex(Indexes.ascending("organizationId"));
//        dbCollection.createIndex(Indexes.ascending("projectId"));
//        logger.info(E.BLUE_DOT+E.BLUE_BIRD+ "Indexes created for SettingsModel");
//
//    }
//
//    private void createProjectIndexes() {
//        //add index
//        MongoCollection<Document> dbCollection = db.getCollection(Project.class.getSimpleName());
//
//        dbCollection.createIndex(Indexes.ascending("organizationId", "name"),
//                new IndexOptions().unique(true));
//
//        dbCollection.createIndex(Indexes.ascending("organizationId"));
//        logger.info(E.BLUE_DOT+E.BLUE_BIRD+ "Indexes created for Project");
//
//    }
//
//    private void createProjectSummaryIndexes() {
//        //add index
//        MongoCollection<Document> dbCollection = db.getCollection(ProjectSummary.class.getSimpleName());
//
//        dbCollection.createIndex(Indexes.ascending("organizationId", "date"),
//                new IndexOptions().unique(false));
//
//        dbCollection.createIndex(Indexes.ascending("projectId", "date"),
//                new IndexOptions().unique(false));
//
//        dbCollection.createIndex(Indexes.ascending("organizationId"));
//        dbCollection.createIndex(Indexes.ascending("projectId"));
//        dbCollection.createIndex(Indexes.ascending("date"));
//        logger.info(E.BLUE_DOT+E.BLUE_BIRD+ "Indexes created for ProjectSummary");
//
//    }
//
//    private void createUserIndexes() {
//        //add index
//        MongoCollection<Document> dbCollection = db.getCollection(User.class.getSimpleName());
//
//        dbCollection.createIndex(Indexes.ascending("email"),
//                new IndexOptions().unique(false));
//
//        dbCollection.createIndex(Indexes.ascending("cellphone"));
//        dbCollection.createIndex(Indexes.ascending("organizationId"));
//        logger.info(E.BLUE_DOT+E.BLUE_BIRD+ "Indexes created for User");
//    }
//
//    private void createCommunityIndexes() {
//        //add index
//        MongoCollection<Document> dbCollection = db.getCollection(Community.class.getSimpleName());
//
//        dbCollection.createIndex(Indexes.ascending("name", "countryId"),
//                new IndexOptions().unique(true));
//        dbCollection.createIndex(Indexes.ascending("countryId"));
//        logger.info(E.BLUE_DOT+E.BLUE_BIRD+ "Indexes created for Community");
//
//    }
//
//    private void createAudioIndexes() {
//        //add index
//        MongoCollection<Document> dbCollection = db.getCollection(Audio.class.getSimpleName());
//
//        dbCollection.createIndex(Indexes.ascending("projectId"),
//                new IndexOptions().unique(false));
//
//        dbCollection.createIndex(Indexes.ascending("userId"));
//        dbCollection.createIndex(Indexes.geo2dsphere("position"));
//        dbCollection.createIndex(Indexes.ascending("organizationId"));
//        logger.info(E.BLUE_DOT+E.BLUE_BIRD+ "Indexes created for Audio");
//
//    }
//
//    private void createSchedulesIndexes() {
//        //add index
//        MongoCollection<Document> dbCollection = db.getCollection(FieldMonitorSchedule.class.getSimpleName());
//
//        dbCollection.createIndex(Indexes.ascending("projectId"),
//                new IndexOptions().unique(false));
//
//        dbCollection.createIndex(Indexes.ascending("userId"));
//        dbCollection.createIndex(Indexes.geo2dsphere("position"));
//
//        logger.info(E.BLUE_DOT+E.BLUE_BIRD+ "Indexes created for FieldMonitorSchedule");
//
//    }
//
//    private void createStateIndex() {
//        MongoCollection<Document> dbCollection = db.getCollection(State.class.getSimpleName());
//
//        dbCollection.createIndex(Indexes.ascending("countryId", "name"),
//                new IndexOptions().unique(true));
//        logger.info(E.BLUE_DOT+E.BLUE_BIRD+ "Indexes created for State");
//
//    }
//
//    private static final String XX = E.LEAF + " ";

    private void createCityIndexes() {
        //add index
        MongoCollection<Document> dbCollection = db.getCollection(City.class.getSimpleName());

        dbCollection.createIndex(Indexes.geo2dsphere("position"));
        dbCollection.createIndex(Indexes.ascending("stateId"));
        dbCollection.createIndex(Indexes.ascending("countryId"));
        dbCollection.createIndex(Indexes.ascending("countryId", "stateId", "name"),
                new IndexOptions().unique(true));

        logger.info(E.BLUE_DOT + E.BLUE_BIRD + "Indexes created for City");


    }


}
