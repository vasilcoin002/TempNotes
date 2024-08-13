package org.example.tempnotes.notes;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.NoteRequest;
import org.example.tempnotes.DTOs.NotesIdRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/notes")
public class NoteController {
    private final NoteService noteService;

    // TODO rename in Postman the address
    @GetMapping("getActiveNotes")
    public ResponseEntity<?> getUserNotes() {
        try {
            return new ResponseEntity<>(noteService.getActiveUserNotes(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PostMapping("addActiveNote")
    public ResponseEntity<?> addActiveNote(@RequestBody NoteRequest request) {
        try {
            noteService.addActiveNote(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PutMapping("updateActiveNote")
    public ResponseEntity<?> updateActiveNote(@RequestBody NoteRequest request) {
        try {
            noteService.updateActiveNote(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @PutMapping("updateActiveNotesOrder")
    public ResponseEntity<?> updateActiveNotesOrder(@RequestBody NotesIdRequest request) {
        try {
            noteService.updateActiveNotesOrder(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @DeleteMapping("deleteActiveNote")
    public ResponseEntity<?> deleteActiveNote(@RequestParam Long id) {
        try {
            noteService.deleteActiveNote(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @DeleteMapping("deleteActiveNotes")
    public ResponseEntity<?> deleteActiveNotes(@RequestBody NotesIdRequest request) {
        try {
            noteService.deleteActiveNotes(request);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
