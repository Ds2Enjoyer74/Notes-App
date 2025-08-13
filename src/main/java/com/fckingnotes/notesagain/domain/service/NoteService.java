package com.fckingnotes.notesagain.domain.service;

import com.fckingnotes.notesagain.domain.exception.EntintyNotFoundException;
import com.fckingnotes.notesagain.domain.model.Note;
import com.fckingnotes.notesagain.domain.repository.NoteRepository;
import com.fckingnotes.notesagain.web.dto.NoteApi;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final NoteRepository noteRepository;

    public Note save(NoteApi.CreateDto createDto) {
        try {
            return noteRepository.save(Note.from(createDto));
        } catch (Exception ex) {
            log.error("Some error happened during save and class: %s".formatted(ex.getClass()), ex);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public Note getById(Long noteId) {
        try {
            return noteRepository
                    .findById(noteId)
                    .orElseThrow(() -> new EntintyNotFoundException(String.format("Note with id: %s not found", noteId)));
        } catch (Exception ex) {
            log.error("Some error happened during get by id", ex);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public List<Note> findAll() {
        try {
            List<Note> result = new ArrayList<Note>();

            noteRepository
                    .findAll()
                    .iterator()
                    .forEachRemaining(result::add);

            return result;
        } catch (Exception ex) {
            log.error("Some error happened during get all", ex);
            throw ex;
        }
    }

    public Note update(Long noteId, NoteApi.UpdateDto updateDto) {
        try {
            Note existingNote = getById(noteId);

            if (updateDto == null) {
                return existingNote;
            }

            if (updateDto.getNoteName() != null) {
                existingNote.setNoteName(updateDto.getNoteName());
            }

            if (updateDto.getNoteBody() != null) {
                existingNote.setNoteBody(updateDto.getNoteBody());
            }

            return noteRepository.save(existingNote);
        } catch (Exception ex) {
            log.error("Some error happened during update", ex);
            throw ex;
        }
    }

    public void deleteById(long noteId) {
        try {
            noteRepository.deleteById(noteId);
        } catch (Exception ex) {
            log.error("Some error happened during delete", ex);
            throw ex;
        }
    }
}