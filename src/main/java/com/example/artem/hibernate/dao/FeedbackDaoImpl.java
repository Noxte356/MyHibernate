package com.example.artem.hibernate.dao;

import com.example.artem.hibernate.HibernateSessionFactoryUtil;
import com.example.artem.hibernate.dto.FeedbackDto;
import com.example.artem.hibernate.entity.Feedback;

public class FeedbackDaoImpl implements FeedbackDao {

    @Override
    public FeedbackDto createFeedback(FeedbackDto dto) {
        Feedback feedback = new Feedback();
        feedback.setAssessment(dto.getAssessment());
        HibernateSessionFactoryUtil.getSessionFactory().openSession().saveOrUpdate(feedbackDto);
    }
}
