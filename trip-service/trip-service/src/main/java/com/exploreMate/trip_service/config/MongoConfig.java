package com.exploreMate.trip_service.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        // Extract database name from URI or use a default
        ConnectionString connectionString = new ConnectionString(mongoUri);
        String database = connectionString.getDatabase();
        return database != null ? database : "exploremate";
    }

    @Override
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();
        return MongoClients.create(settings);
    }
}
