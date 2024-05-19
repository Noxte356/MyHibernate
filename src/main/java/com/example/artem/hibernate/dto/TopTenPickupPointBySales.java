package com.example.artem.hibernate.dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopTenPickupPointBySales {
    private int id;
    private String address;
    Timestamp sale_date;
    private Long amount;
}
