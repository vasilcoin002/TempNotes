package org.example.tempnotes.notes;

import org.example.tempnotes.users.User;
import org.example.tempnotes.users.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserService userService;

    public NoteService(NoteRepository noteRepository, UserService userService) {
        this.noteRepository = noteRepository;
        this.userService = userService;
    }

    public Note getNote(String id) {
        Optional<Note> optionalNote = noteRepository.findById(id);
        optionalNote.orElseThrow(() -> new NoSuchElementException("Note with id " + id + " not found"));
        return optionalNote.get();
    }

    public List<Note> getUserNotes(String userId) {
        User user = userService.getUser(userId);
        List<String> notesIdList = user.getNotesIdList();
        if (notesIdList.isEmpty()) {
            return new ArrayList<>();
        }
        return noteRepository.findAllById(notesIdList);
    }

    public Note addNote(NoteBody noteBody) {
        if (noteIsEmpty(noteBody.getTitle(), noteBody.getDescription())) {
            throw new IllegalArgumentException("Title or body must be given");
        }
        if (noteBody.getUserId() == null) {
            throw new IllegalArgumentException("UserId must be given");
        }
        System.out.println(2);
        User user = userService.getUser(noteBody.getUserId());
        System.out.println(3);
        Note note = new Note(
                noteBody.getTitle(),
                noteBody.getDescription(),
                noteBody.getExpirationDate() == null ?
                        null : LocalDate.parse(noteBody.getExpirationDate())
        );
        System.out.println(4);
        note = noteRepository.save(note);
        System.out.println(5);
        List<String> notesIdList = user.getNotesIdList();
        System.out.println(6);
        notesIdList.add(note.getId());
        System.out.println(7);
        user.setNotesIdList(notesIdList);
        System.out.println(8);
        userService.updateUser(user);
        System.out.println(9);
        return note;
    }

    public void deleteNote(String id) {
        if (id == null) {
            throw new IllegalArgumentException("The id mustn't be null");
        }
        noteRepository.deleteById(id);
        // TODO remove indexes of notes from user obj when note deletes
        // TODO connect deleteNote method with user
    }

    public Note updateNote(NoteBody noteBody) throws IllegalArgumentException {
        if (noteBody.getId() == null) {
            throw new IllegalArgumentException("The id attr mustn't be null");
        }
        if (noteIsEmpty(noteBody.getTitle(), noteBody.getDescription())) {
            throw new IllegalArgumentException("Title or body mustn't be empty");
        } else {
            Note prevNote = getNote(noteBody.getId());

            prevNote.setTitle(noteBody.getTitle());
            prevNote.setDescription(noteBody.getDescription());
            prevNote.setExpirationDate(LocalDate.parse(noteBody.getExpirationDate()));
            return noteRepository.save(prevNote);
        }
    }

    private boolean noteIsEmpty(String title, String description) {
        return title.isEmpty() && description.isEmpty();
    }
}
