package com.shivam.productservice.inheritancetypesdemo.singletable;

import jakarta.persistence.*;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.INTEGER)
@Entity(name = "st_user")
public class User {
    @Id
    long id;
    String email;
}
