package com.shivam.productservice.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity(name = "products")
public class Product extends BaseModel implements Serializable {
    private String title;
    private Double price;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Category category;
    private String description;
    private String imageUrl;
}
