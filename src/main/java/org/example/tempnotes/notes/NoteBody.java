package org.example.tempnotes.notes;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.Date;

@Data
public class NoteBody {
    private String id;
    private String userId;
    // TODO delete userId when JWT token will be working
    private String title;
    private String description;
    private String expiresAt;

    public NoteBody(String id, String title, String description, String expiresAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.expiresAt = expiresAt;
    }
}
