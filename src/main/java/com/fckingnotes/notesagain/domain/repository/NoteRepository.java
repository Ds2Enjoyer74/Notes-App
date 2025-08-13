package com.fckingnotes.notesagain.domain.repository;

import com.fckingnotes.notesagain.domain.model.Note;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends CrudRepository<Note, Long> {
    Note getById(Long id);
}
