package com.boha.kasietransie.data;

import com.boha.kasietransie.data.dto.Vehicle;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import util.E;

import java.util.logging.Logger;

@Data
@Document("TranslationBag")
public class TranslationBag {
    private String _partitionKey;
    @Id
    private String _id;
    private String stringToTranslate;
    private String translatedString;
    private String source;
    private String target;
    private String format;
    private String translatedText;
    private String key;
    private String created;

    private static final Logger logger = Logger.getLogger(TranslationBag.class.getSimpleName());
    private static final String XX = E.COFFEE + E.COFFEE + E.COFFEE;

    public static void createIndex(MongoDatabase db) {
        MongoCollection<org.bson.Document> dbCollection =
                db.getCollection(TranslationBag.class.getSimpleName());

        dbCollection.createIndex(
                Indexes.ascending("source"));

    }
}
