package com.example.artem.service;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProductDto {
    private int productId;
    private int pickupPointId;
    private int count;

}
