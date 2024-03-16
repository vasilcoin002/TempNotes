package org.example.tempnotes.notes;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "notes")
public class Note {
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDate expiresAt;

    public Note(String title, String description, LocalDate expiresAt) {
        this.title = title;
        this.description = description;
        this.expiresAt = expiresAt;
    }
}
