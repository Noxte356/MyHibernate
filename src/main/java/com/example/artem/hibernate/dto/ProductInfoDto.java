package com.example.artem.hibernate.dto;

import com.example.artem.jdbc.ProductDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInfoDto {
    private int id;
    private String name;
    List<FeedbackDto> productList;
    private double average;
}
