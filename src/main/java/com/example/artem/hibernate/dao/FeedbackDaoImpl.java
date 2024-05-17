package com.example.artem.hibernate.dao;

import com.example.artem.hibernate.HibernateSessionFactoryUtil;
import com.example.artem.hibernate.dto.FeedbackDto;
import com.example.artem.hibernate.dto.ProductInfoDto;
import com.example.artem.hibernate.dto.TopTenProductDto;
import com.example.artem.hibernate.entity.Feedback;
import com.example.artem.hibernate.entity.Product;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeedbackDaoImpl implements FeedbackDao {

    @Override
    public FeedbackDto createFeedback(FeedbackDto dto) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.getTransaction();
            transaction.begin();
            Feedback feedback = new Feedback();
            feedback.setAssessment(dto.getAssessment());
            feedback.setAuthor(dto.getAuthor());
            feedback.setComment(dto.getComment());
            Product select = session.get(
                Product.class,
                dto.getProductId()
            );

            log.debug("{}", select.getId());
            feedback.setProduct(select);
            session.saveOrUpdate(feedback);
            log.debug("{}", feedback.getId());
            dto.setFeedbackId(feedback.getId());
            transaction.commit();
            return dto;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return null;
        }
    }

    @Override
    public List<TopTenProductDto> getTopTen(String parameter) {

        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            Query<TopTenProductDto> query = null;

            if (parameter.equals("ASC")) {
                query = session.createNativeQuery(
                    "SELECT p.id, p.name, round(avg(f.assessment)) as average from" +
                        " product p INNER JOIN feedback f on p.id = f.id group by p.id " +
                        "order by average limit 10");
            } else if (parameter.equals("DESC")) {
                query = session.createNativeQuery(
                    "SELECT p.id, p.name, round(avg(f.assessment)) as average from" +
                        " product p INNER JOIN feedback f on p.id = f.id group by p.id " +
                        "order by average desc limit 10");
            } else {
                log.error("Параметр введен неверно - {}", parameter);
            }


            query.setResultListTransformer(Transformers.aliasToBean(TopTenProductDto.class));
            return query.getResultList();
        } catch (HibernateException e) {
            log.error("Ошибка -  ", e);
            return Collections.emptyList();
        }
    }

    @Override
    public ProductInfoDto getProductAndFeedbacks(int id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            session.createNativeQuery("SELECT product.id, feedback.comment, feedback.id"")
        }
        return null;
    }
}
