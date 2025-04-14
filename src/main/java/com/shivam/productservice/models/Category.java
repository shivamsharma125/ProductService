package com.shivam.productservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity(name = "categories")
public class Category extends BaseModel implements Serializable {
//    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
//    private List<Product> products;
    private String title;
}
