package com.shivam.productservice.advices;

import com.shivam.productservice.dtos.ExceptionDto;
import com.shivam.productservice.exceptions.InvalidProductIdException;
import com.shivam.productservice.exceptions.ProductNotFoundException;
import com.shivam.productservice.exceptions.UnAuthorizedUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler({RuntimeException.class, InvalidProductIdException.class})
    public ResponseEntity<ExceptionDto> handleRuntimeException(Exception ex){
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(ex.getMessage());
        exceptionDto.setCode(400);
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleProductNotFoundException(Exception ex){
        ExceptionDto exceptionDto = new ExceptionDto();
        exceptionDto.setMessage(ex.getMessage());
        exceptionDto.setCode(404);
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UnAuthorizedUserException.class)
    public ResponseEntity<String> handleUnAuthorizedException(Exception ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}