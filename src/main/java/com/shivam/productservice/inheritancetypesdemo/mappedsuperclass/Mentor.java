package com.shivam.productservice.inheritancetypesdemo.mappedsuperclass;

import jakarta.persistence.Entity;

@Entity(name = "msc_mentor")
public class Mentor extends User {
    double ratings;
}
