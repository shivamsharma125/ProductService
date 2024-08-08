package com.shivam.productservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionDto {
    private String message;
    private int code;
}
