package com.shivam.productservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@Entity(name = "products")
public class Product extends BaseModel {
    @Serial
    private static final long serialVersionUID = 1L;
    private String title;
    private String description;
    private Double price;
    private String imageUrl;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private Category category;
    private Boolean isPremium;
}
