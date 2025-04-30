package com.shivam.productservice.inheritancetypesdemo.mappedsuperclass;

import jakarta.persistence.Entity;

@Entity(name = "msc_ta")
public class Ta extends User {
    int helpRequests;
}
