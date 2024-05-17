package com.example.artem.hibernate.dto;

import com.example.artem.jdbc.ProductDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackDto {
    private int feedbackId;
    private String comment;
    private String author;
    private int assessment;
    private ProductDto product;
}
