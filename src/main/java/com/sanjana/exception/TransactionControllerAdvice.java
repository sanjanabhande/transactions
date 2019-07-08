package com.sanjana.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TransactionControllerAdvice {

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity catchInvalidFormatException(){
        return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
