package com.example.artem.hibernate.dao;

import com.example.artem.hibernate.dto.FeedbackDto;

public interface FeedbackDao {
    FeedbackDto createFeedback(FeedbackDto feedbackDto);
}
