package org.example.tempnotes.notes;

import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.NoteRequest;
import org.example.tempnotes.DTOs.UpdateUserNotesOrderRequest;
import org.example.tempnotes.auth.AuthenticationService;
import org.example.tempnotes.users.User;
import org.example.tempnotes.users.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserService userService;

    public Note getNote(String id) {
        Optional<Note> optionalNote = noteRepository.findById(id);
        optionalNote.orElseThrow(() -> new NoSuchElementException("Note with id " + id + " not found"));
        return optionalNote.get();
    }

    public List<Note> getUserNotes() {
        User user = userService.getAuthenticatedUser();
        List<String> notesIdList = user.getNotesIdList();
        if (notesIdList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Note> unsortedNotesList = noteRepository.findAllById(notesIdList);
        List<Note> sortedNotesList = new ArrayList<>();
        for (int i = 0; i < unsortedNotesList.size(); i++) {
            int finalI = i;
            sortedNotesList.add(
                unsortedNotesList.stream().filter(note -> Objects.equals(
                    note.getId(),
                    notesIdList.get(finalI))
                ).findFirst().orElse(null));
        }
        return sortedNotesList;
    }

    public Note addNote(NoteRequest noteRequest) {
        checkNoteRequest(noteRequest);

        User user = userService.getAuthenticatedUser();
        Note note = Note.builder()
                            .title(noteRequest.getTitle())
                            .description(noteRequest.getDescription())
                        .build();
        if (noteRequest.getExpirationDate() != null) {
            note.setExpirationDate(LocalDate.parse(noteRequest.getExpirationDate()));
        }
        note = noteRepository.save(note);
        List<String> notesIdList = user.getNotesIdList();
        notesIdList.add(note.getId());
        user.setNotesIdList(notesIdList);
        user = userService.updateUser(user);
        userService.setAuthenticatedUser(user);
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

    public Note updateNote(NoteRequest noteRequest) throws IllegalArgumentException {
        if (noteRequest.getId() == null) {
            throw new IllegalArgumentException("The id attr mustn't be null");
        }
        if (noteIsEmpty(noteRequest.getTitle(), noteRequest.getDescription())) {
            throw new IllegalArgumentException("Title or body mustn't be empty");
        }

        Note prevNote = getNote(noteRequest.getId());

        prevNote.setTitle(noteRequest.getTitle());
        prevNote.setDescription(noteRequest.getDescription());
        prevNote.setExpirationDate(getLocalDateOrNullFromString(noteRequest.getExpirationDate()));
        return noteRepository.save(prevNote);
    }

    public List<String> updateUserNotesOrder(UpdateUserNotesOrderRequest userNotesOrderBody) {
        List<String> newNotesIdList = userNotesOrderBody.getNewNotesIdList();
        if (newNotesIdList == null || newNotesIdList.isEmpty()) {
            throw new IllegalArgumentException("The newNotesIdList attr mustn't be null or empty");
        }
        User user = userService.getAuthenticatedUser();
        user.setNotesIdList(newNotesIdList);
        user = userService.updateUser(user);
        return user.getNotesIdList();
    }

    private boolean noteIsEmpty(String title, String description) {
        return title.isEmpty() && description.isEmpty();
    }

    private void checkNoteRequest(NoteRequest request) {
        if (request.getTitle() == null) {
            throw new IllegalArgumentException("title is not provided(provide at least an empty string \"\")");
        }
        if (request.getDescription() == null) {
            throw new IllegalArgumentException("description is not provided(provide at least an empty string \"\")");
        }
        if (noteIsEmpty(request.getTitle(), request.getDescription())) {
            throw new IllegalArgumentException(
                    "One of title or description mustn't be empty. Provide not empty title or description"
            );
        }
    }

    private LocalDate getLocalDateOrNullFromString(String date) {
        if (date != null) {
            return LocalDate.parse(date);
        } else {
            return null;
        }
    }
}
