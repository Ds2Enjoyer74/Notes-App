package com.fckingnotes.notesagain.web.controller;

import com.fckingnotes.notesagain.domain.exception.EntintyNotFoundException;
import com.fckingnotes.notesagain.web.dto.NoteApi;
import jakarta.validation.ConstraintViolationException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class NoteControllerAdvice {

    @ExceptionHandler(EntintyNotFoundException.class)
    public ResponseEntity<NoteApi.ErrorDto> notFound(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(NoteApi.error(ex.getMessage(), NoteApi.ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler({ IllegalArgumentException.class, ConstraintViolationException.class, DbActionExecutionException.class })
    public ResponseEntity<NoteApi.ErrorDto> badRequest(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(NoteApi.error(ex.getMessage(), NoteApi.ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<NoteApi.ErrorDto> baseHandler(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(NoteApi.error(ex.getMessage(), NoteApi.ErrorCode.UNDEFINED));
    }
}
