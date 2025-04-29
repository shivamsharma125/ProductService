package com.shivam.productservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String title;
    private String description;
}
