package com.example.artem.jdbc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddProductDto {
    private String name;
    private Integer price;
    private Integer count;
    private Integer pickupPointId;
}
