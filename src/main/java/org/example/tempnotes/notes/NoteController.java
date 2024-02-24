package org.example.tempnotes.notes;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public Note getNote(@RequestParam(name = "id") String id) {
        return noteService.getNote(id);
    }

    @PostMapping
    public void addNote(@RequestBody NoteBody noteBody) {
        noteService.addNote(noteBody);
    }

    @DeleteMapping
    public void deleteNote(@RequestParam(name = "id") String id) {
        noteService.deleteNote(id);
    }

    @PutMapping
    public void updateNote(@RequestBody NoteBody noteBody) {
        noteService.updateNote(noteBody);
    }
}
