package edu.northeastern.cs5500.moviebot.service;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import javax.inject.Inject;
import lombok.Getter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoDBService {

    public static String getMongoDBURL() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        final String MongoDBURL = processBuilder.environment().get("MONGODB_URL");
        if (MongoDBURL != null) {
            return MongoDBURL;
        }
        return "mongodb://localhost:27017/movieBot";
    }

    @Getter private MongoDatabase mongoDatabase;

    @Inject
    public MongoDBService() {
        CodecRegistry codecRegistry =
                fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        ConnectionString connectionString = new ConnectionString(getMongoDBURL());

        MongoClientSettings mongoClientSettings =
                MongoClientSettings.builder()
                        .codecRegistry(codecRegistry)
                        .applyConnectionString(connectionString)
                        .build();

        MongoClient mongoClient = MongoClients.create(mongoClientSettings);
        mongoDatabase = mongoClient.getDatabase(connectionString.getDatabase());
    }
}
