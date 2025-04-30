package com.shivam.productservice.inheritancetypesdemo.singletable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = "st_instructor")
@DiscriminatorValue(value = "3")
public class Instructor extends User {
    String company;
}
