package com.shivam.productservice.advices;

import com.shivam.productservice.dtos.ExceptionDto;
import com.shivam.productservice.exceptions.ElementNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDto> handleRuntimeException(RuntimeException ex){
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(ex.getMessage());
        exceptionDto.setCode(404);
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleProductNotFoundException(ElementNotFoundException ex){
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(ex.getMessage());
        exceptionDto.setCode(404);
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }
}