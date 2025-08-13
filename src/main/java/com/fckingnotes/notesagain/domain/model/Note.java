package com.fckingnotes.notesagain.domain.model;

import com.fckingnotes.notesagain.web.dto.NoteApi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notes")
public class Note {
    //Конструктор
    public Note(String noteBody, String noteName) {
        this.noteBody = noteBody;
        this.noteName = noteName;
    }

    @Id
    private Long id;
    private String noteName;
    private String noteBody;

    public static Note from(NoteApi.CreateDto createDto) {
        return new Note(
                createDto.getNoteBody(),
                createDto.getNoteName()
        );
    }
}