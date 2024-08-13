package org.example.tempnotes.notes.repositories;

import org.example.tempnotes.notes.models.ArchivedNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivedNoteRepository extends JpaRepository<ArchivedNote, Long> {
}
