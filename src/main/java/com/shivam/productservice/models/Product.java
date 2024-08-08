package com.shivam.productservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "products")
public class Product extends BaseModel {
    private String title;
    private Double price;
    @ManyToOne
    private Category category;
    private String description;
    private String image;
}
