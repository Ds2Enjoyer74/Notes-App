package com.fckingnotes.notesagain.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public sealed class NoteApi permits NoteApi.UpdateDto, NoteApi.CreateDto, NoteApi.ErrorDto {
    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static final class CreateDto extends NoteApi {
        @JsonProperty(required = true) private String noteName;
        @JsonProperty(required = true) private String noteBody;
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static final class UpdateDto extends NoteApi {
        private String noteName;
        private String noteBody;
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode
    public static final class ErrorDto extends NoteApi {
        private String message;
        private ErrorCode code;
    }

    public static CreateDto createDto(
            String noteName,
            String noteBody
    ) {
        return new CreateDto(
                noteName,
                noteBody
        );
    }

    public static UpdateDto updateDto(String noteName, String noteBody) {
        return new UpdateDto(
                noteName,
                noteBody
        );
    }

    public static ErrorDto error(String message, ErrorCode code) {
        return new ErrorDto(message, code);
    }

    public enum ErrorCode {
        NOT_FOUND,
        BAD_REQUEST,
        UNDEFINED
    }
}

