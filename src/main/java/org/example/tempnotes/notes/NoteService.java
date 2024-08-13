package org.example.tempnotes.notes;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.NoteRequest;
import org.example.tempnotes.DTOs.NotesIdRequest;
import org.example.tempnotes.auth.AuthenticationService;
import org.example.tempnotes.notes.models.ActiveNote;
import org.example.tempnotes.notes.repositories.ActiveNoteRepository;
import org.example.tempnotes.notes.repositories.ArchivedNoteRepository;
import org.example.tempnotes.users.User;
import org.example.tempnotes.users.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final AuthenticationService authenticationService;
    private final ActiveNoteRepository activeNoteRepository;
    private final ArchivedNoteRepository archivedNoteRepository;
    private final UserRepository userRepository;

    public ActiveNote getActiveNote(Long id) {
        return activeNoteRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Note with id " + id + " not found")
        );
    }

    public List<ActiveNote> getActiveUserNotes() {
        return authenticationService.getAuthenticatedUser().getActiveNotes();
    }

    @Transactional
    public void addActiveNote(NoteRequest request) {
        User user = authenticationService.getAuthenticatedUser();
        // TODO validate request (at least title or description must be filled)
        ActiveNote note = activeNoteRepository.save(
                ActiveNote
                    .builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .expirationDate(request.getExpirationDate())
                        .user(user)
                    .build()
        );
        user.getActiveNotes().add(0, note);
        userRepository.save(user);
    }

    @Transactional
    public void updateActiveNote(NoteRequest request) {
        ActiveNote note = getActiveNote(request.getId());
        if (!isUserHasActiveNote(request.getId())) {
            throw new RuntimeException("You don't have permissions to update this note");
        }

        // TODO validate request (at least title or description must be filled)
        note.setTitle(request.getTitle());
        note.setDescription(request.getDescription());
        note.setExpirationDate(request.getExpirationDate());
        activeNoteRepository.save(note);
    }

    @Transactional
    public void updateActiveNotesOrder(NotesIdRequest request) {
        if (!isUserHasSameActiveNotes(request.getNotesIdList())) {
            throw new RuntimeException("Provide the notesIdList with same notes but reordered");
        }
        // creating noteHashMap to make it easy replacing notes in activeNotes
        HashMap<Long, ActiveNote> noteHashMap = new HashMap<>();
        User user = authenticationService.getAuthenticatedUser();
        // filling noteHashMap
        user.getActiveNotes().forEach(
                note -> noteHashMap.put(note.getId(), note)
        );

        user.getActiveNotes().clear();
        // adding notes to activeNotes in correct order
        request.getNotesIdList().forEach(
                id -> user.getActiveNotes().add(noteHashMap.get(id))
        );
        userRepository.save(user);
    }

    @Transactional
    public void deleteActiveNote(Long id) {
        User user = authenticationService.getAuthenticatedUser();
        if (!user.getActiveNotes().removeIf(note -> Objects.equals(note.getId(), id))) {
            throw new RuntimeException("You don't have permissions to delete this note");
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteActiveNotes(NotesIdRequest request) {
        User user = authenticationService.getAuthenticatedUser();
        // usage of HashSet for better performance
        HashSet<Long> notesIdSet = new HashSet<>();
        user.getActiveNotes().forEach(note -> notesIdSet.add(note.getId()));
        if (!notesIdSet.containsAll(request.getNotesIdList())) {
            throw new RuntimeException("Provide only id of notes which you actually have");
        }

        // deleting requested notes from activeNotes
        List<ActiveNote> activeNotes = user.getActiveNotes().stream()
                .filter(note -> !request.getNotesIdList().contains(note.getId())).collect(Collectors.toList());
        user.setActiveNotes(activeNotes);
        userRepository.save(user);
    }

    private boolean isUserHasSameActiveNotes(List<Long> notesIdList) {
        User user = authenticationService.getAuthenticatedUser();
        HashSet<Long> persistentNotesIdSet = new HashSet<>(
                // getting list of persistent notes id
                user.getActiveNotes().stream().map(note -> note.getId()).toList()
        );
        HashSet<Long> notesIdSet = new HashSet<>(notesIdList);
        return persistentNotesIdSet.equals(notesIdSet);
    }

    private boolean isUserHasActiveNote(Long id) {
        User user = authenticationService.getAuthenticatedUser();
        return user.getActiveNotes().stream()
                .map(ActiveNote::getId)
                .toList()
                .contains(id);
    }


//    private LocalDate getLocalDateOrNullFromString(String dateString) {
//        if (dateString == null) {return null;}
//        return LocalDate.parse(dateString);
//    }
}
