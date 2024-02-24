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
        Note note = new Note(
                noteBody.getTitle(),
                noteBody.getBody(),
                LocalDate.parse(noteBody.getDestroyAtTime())
        );
        noteRepository.save(note);
    }

    public void deleteNote(String id) {
        noteRepository.deleteById(id);
    }

    public void updateNote(NoteBody noteBody) throws IllegalArgumentException {
        if (noteBody.getId() == null) {
            throw new IllegalArgumentException("the id field mustn't be null");
        }
        if (noteBody.getTitle() == null) {
            throw new IllegalArgumentException("the title field mustn't be null");
        }

        Note prevNote = getNote(noteBody.getId());

        prevNote.setTitle(noteBody.getTitle());
        prevNote.setBody(noteBody.getBody());
        prevNote.setDestroyAtTime(LocalDate.parse(noteBody.getDestroyAtTime()));
        noteRepository.save(prevNote);
    }
}
