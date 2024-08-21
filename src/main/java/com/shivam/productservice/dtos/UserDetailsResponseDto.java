package com.shivam.productservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailsResponseDto {
    private UserDto user;
    private String failureMessage;
    private int statusCode;
}