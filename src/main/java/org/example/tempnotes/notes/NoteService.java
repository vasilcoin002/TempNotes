package org.example.tempnotes.notes;

import lombok.RequiredArgsConstructor;
import org.example.tempnotes.DTOs.DeleteUserNotesRequest;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserService userService;

    public Note getNote(String id) {
        // TODO add checking if note belongs to user
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
                            .expirationDate(getLocalDateOrNullFromString(noteRequest.getExpirationDate()))
                        .build();
        note = noteRepository.save(note);
        List<String> notesIdList = user.getNotesIdList();
        notesIdList.add(note.getId());
        userService.updateUserNotesIdList(notesIdList);
        return note;
    }

    public void deleteNote(String id) {
        if (id == null) {
            throw new IllegalArgumentException("The id is not provided");
        }
        List<String> notesIdList = getNotesId(getUserNotes());
        if (!notesIdList.remove(id)) {
            throw new IllegalArgumentException("User doesn't have the note with id " + id);
        }
        userService.updateUserNotesIdList(notesIdList);
        noteRepository.deleteById(id);
    }

    public List<String> deleteNotes(DeleteUserNotesRequest request) {
        if (request.getNotesIdList() == null) {
            throw new IllegalArgumentException("The notesIdList is not provided");
        }
        return deleteNotes(request.getNotesIdList());
    }

    public List<String> deleteNotes(List<String> deleteNotesIdList) {
        if (deleteNotesIdList.isEmpty()) {
            throw new IllegalArgumentException("The notesIdList mustn't be empty");
        }
        List<String> notesIdList = getNotesId(getUserNotes());
        if (!new HashSet<>(notesIdList).containsAll(deleteNotesIdList)) {
            throw new IllegalArgumentException("Provided id of notes which user doesn't have");
        }
        notesIdList.removeAll(deleteNotesIdList);
        notesIdList = userService.updateUserNotesIdList(notesIdList);
        noteRepository.deleteAllById(deleteNotesIdList);
        return notesIdList;
    }

    public Note updateNote(NoteRequest noteRequest) throws IllegalArgumentException {
        if (noteRequest.getId() == null) {
            throw new IllegalArgumentException("The id is not provided");
        }
        checkNoteRequest(noteRequest);
        Note note = getNote(noteRequest.getId());
        if (
                note.getTitle().equals(noteRequest.getTitle()) &&
                note.getDescription().equals(noteRequest.getDescription()) &&
                Objects.equals(note.getExpirationDate(), getLocalDateOrNullFromString(noteRequest.getExpirationDate()))
        ) {
            throw new IllegalArgumentException("Provided the same title, description and expirationDate as note has");
        }

        note.setTitle(noteRequest.getTitle());
        note.setDescription(noteRequest.getDescription());
        note.setExpirationDate(getLocalDateOrNullFromString(noteRequest.getExpirationDate()));
        return noteRepository.save(note);
    }

    public List<String> updateUserNotesOrder(UpdateUserNotesOrderRequest userNotesOrderBody) {
        List<String> newNotesIdList = userNotesOrderBody.getNewNotesIdList();
        if (newNotesIdList == null) {
            throw new IllegalArgumentException("The notesIdList is not provided");
        }
        // checking if all the previous notes are in list but reordered
        User user = userService.getAuthenticatedUser();
        List<String> notesIdList = user.getNotesIdList();
        checkUpdateUserNotesOrder(notesIdList, newNotesIdList);

        return userService.updateUserNotesIdList(newNotesIdList);
    }

    private boolean noteIsEmpty(String title, String description) {
        return title.isEmpty() && description.isEmpty();
    }

    private void checkNoteRequest(NoteRequest request) {
        if (request.getTitle() == null) {
            throw new IllegalArgumentException("Title is not provided(provide at least an empty string \"\")");
        }
        if (request.getDescription() == null) {
            throw new IllegalArgumentException("Description is not provided(provide at least an empty string \"\")");
        }
        if (noteIsEmpty(request.getTitle(), request.getDescription())) {
            throw new IllegalArgumentException(
                    "One of title or description mustn't be empty. Provide not empty title or description"
            );
        }
    }

    private void checkUpdateUserNotesOrder(List<String> notesIdList, List<String> newNotesIdList) {
        if (notesIdList.equals(newNotesIdList)) {
            throw new IllegalArgumentException("Provided the same notesIdList as user has");
        }
        HashSet<String> notesIdSet = new HashSet<>(notesIdList);
        HashSet<String> newNotesIdSet = new HashSet<>(newNotesIdList);
        System.out.println(notesIdSet);
        System.out.println(newNotesIdSet);
        if (!notesIdSet.equals(newNotesIdSet)) {
            throw new IllegalArgumentException("Not all the notes which user has are provided");
        }
    }

    private LocalDate getLocalDateOrNullFromString(String dateString) {
        if (dateString != null) {
            return LocalDate.parse(dateString);
        } else {
            return null;
        }
    }

    private List<String> getNotesId(List<Note> notesList) {
        return getUserNotes().stream().map(Note::getId).collect(Collectors.toList());
    }
}
