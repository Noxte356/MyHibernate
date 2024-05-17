package com.example.artem.hibernate.dao;

import com.example.artem.hibernate.dto.FeedbackDto;
import com.example.artem.hibernate.dto.ProductInfoDto;
import com.example.artem.hibernate.dto.TopTenProductDto;
import java.util.List;

public interface FeedbackDao {
    FeedbackDto createFeedback(FeedbackDto feedbackDto);

    List<TopTenProductDto> getTopTen(String parameter);

    ProductInfoDto getProductAndFeedbacks(int id);
}
