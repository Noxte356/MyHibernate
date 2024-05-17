package com.example.artem.jdbc;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResultPickupPointWithAllProductDto {
    private int pickupPointId;
    private String addressName;
    List<ProductDto> inventory;
}
