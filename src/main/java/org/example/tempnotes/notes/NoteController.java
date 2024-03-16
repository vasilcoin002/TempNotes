package org.example.tempnotes.notes;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    @GetMapping("note")
    public ResponseEntity<?> getNote(@RequestParam String id) {
        try {
            return new ResponseEntity<>(noteService.getNote(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("userNotes")
    public ResponseEntity<?> getUserNotes(@RequestParam String userId) {
        try {
            return new ResponseEntity<>(noteService.getUserNotes(userId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> addNote(@RequestBody NoteBody noteBody) {
        try {
            return new ResponseEntity<>(noteService.addNote(noteBody), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteNote(@RequestParam String id) {
        try {
            noteService.deleteNote(id);
            return new ResponseEntity<>("Note with id " + id + " has been successfully deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateNote(@RequestBody NoteBody noteBody) {
        try {
            return new ResponseEntity<>(noteService.updateNote(noteBody), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
