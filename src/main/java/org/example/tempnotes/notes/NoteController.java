package org.example.tempnotes.notes;

import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.NoteRequest;
import org.example.tempnotes.DTOs.UpdateUserNotesRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/notes")
public class NoteController {

    private final NoteService noteService;

    @CrossOrigin(origins = "*")
    @GetMapping("getNotes")
    public ResponseEntity<?> getUserNotes() {
        try {
            return new ResponseEntity<>(noteService.getUserNotes(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping("addNote")
    public ResponseEntity<?> addNote(@RequestBody NoteRequest noteRequest) {
        try {
            return new ResponseEntity<>(noteService.addNote(noteRequest), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("deleteNote")
    public ResponseEntity<?> deleteNote(@RequestParam String id) {
        try {
            noteService.deleteNote(id);
            return new ResponseEntity<>("Note with id " + id + " has been successfully deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("deleteNotes")
    public ResponseEntity<?> deleteNotes(@RequestBody UpdateUserNotesRequest request) {
        try {
            return new ResponseEntity<>(noteService.deleteNotes(request), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping("updateNote")
    public ResponseEntity<?> updateNote(@RequestBody NoteRequest noteRequest) {
        try {
            return new ResponseEntity<>(noteService.updateNote(noteRequest), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin(origins = "*")
    @PutMapping("updateUserNotesOrder")
    public ResponseEntity<?> updateUserNotesOrder(@RequestBody UpdateUserNotesRequest userNotesOrderBody) {
        try {
            List<String> newNotesIdList = noteService.updateUserNotesOrder(userNotesOrderBody);
            return new ResponseEntity<>(newNotesIdList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
