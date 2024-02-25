package org.example.tempnotes.notes;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note getNote(String id) {
        Optional<Note> optionalNote = noteRepository.findById(id);
        optionalNote.orElseThrow(() -> new NoSuchElementException("Note with id " + id + " not found"));
        return optionalNote.get();
    }

    public void addNote(NoteBody noteBody) {
        if (
                (noteBody.getTitle() == null || noteBody.getTitle().isEmpty())
                    &&
                (noteBody.getBody() == null || noteBody.getBody().isEmpty())
        ) {
            throw new IllegalArgumentException("Title or body must be given");
        }
        Note note = new Note(
                noteBody.getTitle(),
                noteBody.getBody(),
                LocalDate.parse(noteBody.getDestroyAtTime())
        );
        noteRepository.save(note);
    }

    public void deleteNote(String id) {
        if (id == null) {
            throw new IllegalArgumentException("The id mustn't be null");
        }
        noteRepository.deleteById(id);
    }

    public void updateNote(NoteBody noteBody) throws IllegalArgumentException {
        if (noteBody.getId() == null) {
            throw new IllegalArgumentException("The id field mustn't be null");
        }
        if (
                (noteBody.getTitle() == null || noteBody.getTitle().isEmpty())
                    &&
                (noteBody.getBody() == null || noteBody.getBody().isEmpty())
        ) {
            deleteNote(noteBody.getId());
        } else {
            Note prevNote = getNote(noteBody.getId());

            prevNote.setTitle(noteBody.getTitle());
            prevNote.setBody(noteBody.getBody());
            prevNote.setDestroyAtTime(LocalDate.parse(noteBody.getDestroyAtTime()));
            noteRepository.save(prevNote);
        }
    }
}
