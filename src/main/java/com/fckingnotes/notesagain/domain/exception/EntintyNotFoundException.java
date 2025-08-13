package com.fckingnotes.notesagain.domain.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntintyNotFoundException extends RuntimeException {

    public EntintyNotFoundException(String msg) {
        super(msg);
    }
}
