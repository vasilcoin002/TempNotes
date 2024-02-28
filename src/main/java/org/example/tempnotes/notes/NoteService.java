package org.example.tempnotes.notes;

import org.example.tempnotes.users.User;
import org.example.tempnotes.users.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public Note addNote(NoteBody noteBody) {
        if (noteIsEmpty(noteBody.getTitle(), noteBody.getBody())) {
            throw new IllegalArgumentException("Title or body must be given");
        }
        if (noteBody.getUserId() == null) {
            throw new IllegalArgumentException("UserId must be given");
        }
        User user = userService.getUser(noteBody.getUserId());

        Note note = new Note(
                noteBody.getTitle(),
                noteBody.getBody(),
                LocalDate.parse(noteBody.getDestroyAtTime())
        );
        note = noteRepository.save(note);
        List<String> notesIdList = user.getNotesIdList();
        notesIdList.add(note.getId());
        user.setNotesIdList(notesIdList);
        userService.updateUser(user);
        return note;
    }

    public void deleteNote(String id) {
        if (id == null) {
            throw new IllegalArgumentException("The id mustn't be null");
        }
        noteRepository.deleteById(id);

        // TODO connect deleteNote method with user
    }

    public Note updateNote(NoteBody noteBody) throws IllegalArgumentException {
        if (noteBody.getId() == null) {
            throw new IllegalArgumentException("The id field mustn't be null");
        }
        if (noteIsEmpty(noteBody.getTitle(), noteBody.getBody())) {
            throw new IllegalArgumentException("Title or body mustn't be empty or null");
        } else {
            Note prevNote = getNote(noteBody.getId());

            prevNote.setTitle(noteBody.getTitle());
            prevNote.setBody(noteBody.getBody());
            prevNote.setDestroyAtTime(LocalDate.parse(noteBody.getDestroyAtTime()));
            return noteRepository.save(prevNote);
        }
    }

    private boolean noteIsEmpty(String title, String body) {
        return (title == null || title.isEmpty()) && (body == null || body.isEmpty());
    }
}
