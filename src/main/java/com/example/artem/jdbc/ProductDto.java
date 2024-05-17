package com.example.artem.jdbc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private int productId;
    private String productName;
    private int productPrice;
    private int productQuantity;
}
