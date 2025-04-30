package com.shivam.productservice.inheritancetypesdemo.mappedsuperclass;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User {
    @Id
    long id;
    String email;
}
