package org.example.tempnotes.requestDTO;

import lombok.Data;

@Data
public class NoteBody {
    private String id;
    private String userId;
    // TODO delete userId when JWT token will be working
    private String title;
    private String description;
    private String expirationDate;

    public NoteBody(String id, String title, String description, String expirationDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.expirationDate = expirationDate;
    }
}
