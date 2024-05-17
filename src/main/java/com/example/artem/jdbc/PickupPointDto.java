package com.example.artem.jdbc;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
public class PickupPointDto {
    private int id;
    private String address;
    private Time open;
    private Time close;
}
