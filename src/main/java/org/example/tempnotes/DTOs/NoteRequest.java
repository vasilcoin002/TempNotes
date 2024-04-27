package org.example.tempnotes.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteRequest {
    private String id;
    private String title;
    private String description;
    private String expirationDate;
}
