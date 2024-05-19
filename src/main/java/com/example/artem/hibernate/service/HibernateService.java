package com.example.artem.hibernate.service;

import com.example.artem.hibernate.dao.HibernateDao;
import com.example.artem.hibernate.dto.FeedbackDto;
import com.example.artem.hibernate.dto.ProductInfoDto;
import com.example.artem.hibernate.dto.TopTenPickupPointBySales;
import com.example.artem.hibernate.dto.TopTenProductDto;
import com.example.artem.jdbc.PickupPointDto;
import com.example.artem.jdbc.SaleProductDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HibernateService {

    private final HibernateDao feedbackDao;

    public FeedbackDto create(FeedbackDto dto) {
        return feedbackDao.createFeedback(dto);
    }

    public List<TopTenProductDto> getTopTen(String parameter){
        List<TopTenProductDto> topTen = feedbackDao.getTopTen(parameter);
        return topTen;
    }

    public ProductInfoDto getProductInfo(int id) {
        return feedbackDao.getProductAndFeedbacks(id);
    }

    public boolean saleProduct(SaleProductDto dto) {
        return feedbackDao.saleProduct(dto);
    }

    public List<TopTenPickupPointBySales> getTopTenPickupPointBySales() {
        return feedbackDao.getTopTenPickupPointBySales();
    }

    public List<PickupPointDto> getWhereNameExistParameter(String parameter) {
        return feedbackDao.getWhereNameExistParameter(parameter);
    }

}
