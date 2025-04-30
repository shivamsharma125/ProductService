package com.shivam.productservice.inheritancetypesdemo.tableperclass;

import jakarta.persistence.*;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity(name = "tpc_user")
public class User {
    @Id
    long id;
    String email;
}
