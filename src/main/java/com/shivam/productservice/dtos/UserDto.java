package com.shivam.productservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private List<RoleDto> roles;
    private boolean isEmailVerified;
}
