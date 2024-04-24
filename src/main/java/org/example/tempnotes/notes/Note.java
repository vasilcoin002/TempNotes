package org.example.tempnotes.notes;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
@Data
@Builder
@Document(collection = "notes")
public class Note {
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDate expirationDate;
}
