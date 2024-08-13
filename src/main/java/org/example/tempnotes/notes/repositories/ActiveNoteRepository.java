package org.example.tempnotes.notes.repositories;

import org.example.tempnotes.notes.models.ActiveNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveNoteRepository extends JpaRepository<ActiveNote, Long> {
}
