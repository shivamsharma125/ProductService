package com.shivam.productservice.inheritancetypesdemo.mappedsuperclass;

import jakarta.persistence.Entity;

@Entity(name = "msc_instructor")
public class Instructor extends User {
    String company;
}
