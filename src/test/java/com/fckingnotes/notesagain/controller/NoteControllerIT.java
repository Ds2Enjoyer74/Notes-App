package com.fckingnotes.notesagain.controller;


import com.fckingnotes.notesagain.domain.model.Note;
import com.fckingnotes.notesagain.domain.repository.NoteRepository;
import com.fckingnotes.notesagain.web.dto.NoteApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class NoteControllerIT {

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
    private WebTestClient webTestClient;

    @Autowired
    private NoteRepository noteRepository;

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
    }

    @Test
    void should_save_note_to_db() {
        // given
        NoteApi.CreateDto createDto = NoteApi.createDto(
                "noteName",
                "noteBody"
        );

        // when
        Note result = webTestClient
                .post()
                .uri("/notes")
                .bodyValue(createDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Note.class)
                .returnResult()
                .getResponseBody();

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isGreaterThan(0);
        assertThat(result.getNoteName()).isEqualTo(createDto.getNoteName());
        assertThat(result.getNoteBody()).isEqualTo(createDto.getNoteBody());

    }

    @Test
    void should_find_note_by_id() {
        // given
        Note savedNote = noteRepository.save(
                new Note(
                "test body",
                "test name"
        )
        );

        // when
        Note resp = webTestClient
                .get()
                .uri("/notes/" + savedNote.getId())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Note.class)
                .returnResult()
                .getResponseBody();

        // then
        assertThat(resp.getId()).isEqualTo(savedNote.getId());

        assertThat(resp.getNoteName()).isEqualTo(savedNote.getNoteName());
        assertThat(resp.getNoteBody()).isEqualTo(savedNote.getNoteBody());

    }

    @Test
    void should_find_all() {
        // given
        noteRepository.saveAll(
                List.of(
                        new Note(
                                "test body1",
                                "test name1"
                        ),
                        new Note(
                                "test body2",
                                "test name2"
                        )
                )
        );

        // when
        List<Note> resp = webTestClient
                .get()
                .uri("/notes")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(Note.class)
                .returnResult().getResponseBody();

        // then
        assertThat(resp).hasSize(2);
    }

    @Test
    void should_update() {
        // given
        // 1. Сначала сохраняем заметку в БД
        Note originalNote = noteRepository.save(
                new Note(
                        "Original body",
                        "Original title"
                )
        );

        // 2. Подготавливаем данные для обновления
        NoteApi.UpdateDto updateDto = NoteApi.updateDto(
                "Updated title",
                "Updated body"
        );

        // when
        // 3. Отправляем PATCH-запрос
        Note resp = webTestClient
                .patch()
                .uri("/notes/" + originalNote.getId())
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Note.class)
                .returnResult().getResponseBody();

        // then
        // 4. Проверяем ответ
        // Проверяем, что ID остался прежним
        assertThat(resp.getId()).isEqualTo(originalNote.getId());

        // Проверяем обновленные поля
        assertThat(resp.getNoteName()).isEqualTo(updateDto.getNoteName());
        assertThat(resp.getNoteBody()).isEqualTo(updateDto.getNoteBody());
    }

    @Test
    void should_delete_by_id() {
        // given
        Note savedNote = noteRepository.save(new Note(
                "noteBody",
                "noteName"
        ));

        // when & then
        webTestClient
                .delete()
                .uri("/notes/" + savedNote.getId())
                .exchange()
                .expectStatus().isNoContent();
    }


    @Test
    void should_send_404_if_note_not_exists() {
        // given & when
        NoteApi.ErrorDto errorResp = webTestClient
                .get()
                .uri("/notes/123")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(NoteApi.ErrorDto.class)
                .returnResult().getResponseBody();

        // then
        assertThat(errorResp.getMessage()).isEqualTo("Note with id: 123 not found");
        assertThat(errorResp.getCode()).isEqualTo(NoteApi.ErrorCode.NOT_FOUND);
    }

    @Test
    void should_send_500_if_internal_server_error() {
        // given & when
        NoteApi.ErrorDto errorResp = webTestClient
                .get()
                .uri("/not-exists")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(NoteApi.ErrorDto.class)
                .returnResult().getResponseBody();


        // then
        assertThat(errorResp.getMessage()).contains("No static resource not-exists");
        assertThat(errorResp.getCode()).isEqualTo(NoteApi.ErrorCode.UNDEFINED);
    }

    @Test
    void should_send_400_if_bad_request() {
        // given
        NoteApi.CreateDto createDto = NoteApi.createDto(
                null,
                "pepka"
        );

        // when
        NoteApi.ErrorDto errorResp = webTestClient
                .post()
                .uri("/notes")
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(NoteApi.ErrorDto.class)
                .returnResult().getResponseBody();

        // then
        assertThat(errorResp.getMessage()).isNotEmpty();
        assertThat(errorResp.getCode()).isEqualTo(NoteApi.ErrorCode.BAD_REQUEST);
    }
}


