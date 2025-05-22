package com.greatlearning.todo.config;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    // Inject Firebase database URL from application.properties
    @Value("${firebase.database.url}")
    private String firebaseDatabaseUrl;

    @PostConstruct
    public void initialize() {
        try {
            // Load service account JSON from resources
            ClassPathResource serviceAccount = new ClassPathResource("serviceAccountKey.json");

            try (InputStream serviceAccountStream = serviceAccount.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                        .setDatabaseUrl(firebaseDatabaseUrl)
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
