package org.example.tempnotes.notes.models;

import org.example.tempnotes.DTOs.NoteRequest;
import org.example.tempnotes.users.User;

import java.time.LocalDate;

public abstract class Note {

    abstract Long getId();
    abstract void setId(Long id);
    abstract String getTitle();
    abstract void setTitle(String title);
    abstract String getDescription();
    abstract void setDescription(String description);
    abstract LocalDate getExpirationDate();
    abstract void setExpirationDate(LocalDate expirationDate);
    abstract User getUser();
    abstract void setUser(User user);

    @Override
    public String toString() {
        return "Note" +
                "(id=" + getId().toString() +
                ", title=" + getTitle() +
                ", description=" + getDescription() +
                ", userId=" + getUser().getId().toString() +
                ")";
    }
}