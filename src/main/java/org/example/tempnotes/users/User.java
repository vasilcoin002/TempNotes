package org.example.tempnotes.users;

import lombok.Data;
import org.example.tempnotes.notes.Note;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private List<String> notesIdList;

    public User(String email, String password, List<String> notesIdList) {
        this.email = email;
        this.password = password;
        this.notesIdList = notesIdList;
    }
}
