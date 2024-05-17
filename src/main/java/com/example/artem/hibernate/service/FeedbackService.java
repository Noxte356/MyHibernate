package com.example.artem.hibernate.service;

import com.example.artem.hibernate.dao.FeedbackDao;
import com.example.artem.hibernate.dto.FeedbackDto;
import com.example.artem.hibernate.dto.TopTenProductDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackDao feedbackDao;

    public FeedbackDto create(FeedbackDto dto) {
        return feedbackDao.createFeedback(dto);
    }

    public List<TopTenProductDto> getTopTen(String parameter){
        List<TopTenProductDto> topTen = feedbackDao.getTopTen(parameter);
        return topTen;
    }

}
