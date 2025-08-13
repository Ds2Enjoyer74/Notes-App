package com.fckingnotes.notesagain.web.controller;

import com.fckingnotes.notesagain.domain.model.Note;
import com.fckingnotes.notesagain.domain.service.NoteService;
import com.fckingnotes.notesagain.web.dto.NoteApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @PostMapping
    public Note save(@RequestBody @Valid NoteApi.CreateDto createDto) {
        return noteService.save(createDto);
    }

    @GetMapping("/{noteId}")
    public Note findByID(@PathVariable Long noteId) {
        return noteService.getById(noteId);
    }

    @GetMapping
    public List<Note> findAll() {
        return noteService.findAll();
    }

    // Крч, чтобы update делать, просто return оставь и поменяй в скобочках
    @PatchMapping("/{noteId}")
    public Note update(@PathVariable Long noteId, @RequestBody @Valid NoteApi.UpdateDto updateDto) {
        return noteService.update(noteId, updateDto);
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteById(@PathVariable Long noteId) {
        noteService.deleteById(noteId);
        return ResponseEntity.noContent().build();
    }


}
