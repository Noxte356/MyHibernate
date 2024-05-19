package com.example.artem.hibernate.dao;

import com.example.artem.hibernate.dto.FeedbackDto;
import com.example.artem.hibernate.dto.ProductInfoDto;
import com.example.artem.hibernate.dto.TopTenPickupPointBySales;
import com.example.artem.hibernate.dto.TopTenProductDto;
import com.example.artem.jdbc.PickupPointDto;
import com.example.artem.jdbc.SaleProductDto;
import java.util.List;

public interface HibernateDao {
    FeedbackDto createFeedback(FeedbackDto feedbackDto);

    List<TopTenProductDto> getTopTen(String parameter);

    ProductInfoDto getProductAndFeedbacks(int id);

    boolean saleProduct(SaleProductDto dto);

    List<TopTenPickupPointBySales> getTopTenPickupPointBySales();

    List<PickupPointDto> getWhereNameExistParameter(String parameter);
}
