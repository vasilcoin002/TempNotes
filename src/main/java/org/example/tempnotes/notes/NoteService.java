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
            throw new IllegalArgumentException("The id mustn't be null");
        }
        User user = userService.getAuthenticatedUser();
        List<String> notesIdList = user.getNotesIdList();
        if (!notesIdList.remove(id)) {
            throw new IllegalArgumentException("Note with id " + id + " not found");
        }
        userService.updateUserNotesIdList(notesIdList);
        noteRepository.deleteById(id);
    }

    // TODO create method deleteNotes

    public Note updateNote(NoteRequest noteRequest) throws IllegalArgumentException {
        if (noteRequest.getId() == null) {
            throw new IllegalArgumentException("The id attr mustn't be null");
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
            throw new IllegalArgumentException("The newNotesIdList attr mustn't be null or empty");
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
}
