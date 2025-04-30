package com.shivam.productservice.inheritancetypesdemo.tableperclass;

import jakarta.persistence.Entity;

@Entity(name = "tpc_ta")
public class Ta extends User {
    int helpRequests;
}
