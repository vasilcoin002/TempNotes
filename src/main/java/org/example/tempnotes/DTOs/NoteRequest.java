package org.example.tempnotes.DTOs;

import lombok.Data;

@Data
public class NoteRequest {
    private String id;
    private String userId;
    // TODO delete userId when JWT token will be working
    private String title;
    private String description;
    private String expirationDate;

    public NoteRequest(String id, String title, String description, String expirationDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.expirationDate = expirationDate;
    }
}
