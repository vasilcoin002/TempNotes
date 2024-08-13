package org.example.tempnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TempNotesApplication {
    // TODO add RefreshToken
    // TODO add different languages(ukrainian, czech)
    // TODO return Response Entities with the normal messages
    public static void main(String[] args) {
        SpringApplication.run(TempNotesApplication.class, args);
    }

}
