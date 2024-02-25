package org.example.tempnotes.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.tempnotes.notes.Note;

import java.util.List;

@Data
@AllArgsConstructor
public class UserBody {
    private String id;
    private String email;
    private String password;
}
