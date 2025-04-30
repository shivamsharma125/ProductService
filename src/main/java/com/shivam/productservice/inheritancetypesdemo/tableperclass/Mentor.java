package com.shivam.productservice.inheritancetypesdemo.tableperclass;

import jakarta.persistence.Entity;

@Entity(name = "tpc_mentor")
public class Mentor extends User {
    double ratings;
}
