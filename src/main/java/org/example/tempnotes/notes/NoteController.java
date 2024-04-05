package org.example.tempnotes.notes;

import org.example.tempnotes.requestBodies.UpdateUserNotesOrderBody;
import org.example.tempnotes.requestBodies.NoteBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("note")
    public ResponseEntity<?> getNote(@RequestParam String id) {
        try {
            return new ResponseEntity<>(noteService.getNote(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("userNotes")
    public ResponseEntity<?> getUserNotes(@RequestParam String userId) {
        try {
            List<Note> notes = noteService.getUserNotes(userId);
            return new ResponseEntity<>(notes, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping
    public ResponseEntity<?> addNote(@RequestBody NoteBody noteBody) {
        try {
            return new ResponseEntity<>(noteService.addNote(noteBody), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping
    public ResponseEntity<?> deleteNote(@RequestParam String id) {
        try {
            noteService.deleteNote(id);
            return new ResponseEntity<>("Note with id " + id + " has been successfully deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping("note")
    public ResponseEntity<?> updateNote(@RequestBody NoteBody noteBody) {
        try {
            return new ResponseEntity<>(noteService.updateNote(noteBody), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping("updateUserNotesOrder")
    public ResponseEntity<?> updateUserNotesOrder(@RequestBody UpdateUserNotesOrderBody userNotesOrderBody) {
        try {
            List<String> res = noteService.updateUserNotesOrder(userNotesOrderBody);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
