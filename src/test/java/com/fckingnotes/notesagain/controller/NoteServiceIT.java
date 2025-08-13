package com.fckingnotes.notesagain.controller;


import com.fckingnotes.notesagain.domain.model.Note;
import com.fckingnotes.notesagain.domain.repository.NoteRepository;
import com.fckingnotes.notesagain.domain.service.NoteService;
import com.fckingnotes.notesagain.web.dto.NoteApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
public class NoteServiceIT {
    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")//not necessary
                    .withUsername("testuser")//not necessary
                    .withPassword("testpass");//not necessary


    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    NoteService noteService;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void sutUp() {
        noteRepository.deleteAll();
    }

    @Test
    void should_delete_by_id() {
        // given
        Note requestBody = new Note(
                "noteBody",
                "noteName"
        );
        Note savedNote = noteRepository.save(requestBody);

        // when
        noteService.deleteById(savedNote.getId());

        // then
        assertThat(noteRepository.findAll()).isEmpty();
    }

    @Test
    void should_find_all() {
        // given
        Note requestBody1 = new Note(
                "noteBody",
                "noteName"
        );
        Note requestBody2 = new Note(
                "asfsfafsasf",
                "nasfsafasfasfe"
        );
        Note savedNote1 = noteRepository.save(requestBody1);
        Note savedNote2 = noteRepository.save(requestBody2);
        // when
        Iterable<Note> result = noteRepository.findAll();
        // then
        assertThat(result).hasSize(2);
    }
    @Test
    void should_find_by_id() {
        // given
        Note requestBody1 = new Note(
                "noteBody",
                "noteName"
        );
        Note expected = noteRepository.save(requestBody1);
        // when
        Note result = noteService.getById(expected.getId());
        // then
        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getNoteBody()).isEqualTo(expected.getNoteBody());
        assertThat(result.getNoteName()).isEqualTo(expected.getNoteName());
    }

    @Test
    void should_update() {
        // given
        Note original = noteRepository.save(
                new Note(
                        "noteBody",
                        "noteName"
                )
        );
        NoteApi.UpdateDto updateDto = NoteApi.updateDto("updated name", "updated body");

        // when
        Note res = noteService.update(original.getId(), updateDto);

        assertThat(res).satisfies(actual -> {
            assertThat(actual.getId()).isEqualTo(original.getId());
            assertThat(actual.getNoteName()).isEqualTo(updateDto.getNoteName());
            assertThat(actual.getNoteBody()).isEqualTo(updateDto.getNoteBody());
        });
    }
}
