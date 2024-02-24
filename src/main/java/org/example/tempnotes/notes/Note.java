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
    private String body;
    private LocalDate destroyAtTime;

    public Note(String title, String body, LocalDate destroyAtTime) {
        this.title = title;
        this.body = body;
        this.destroyAtTime = destroyAtTime;
    }
}
