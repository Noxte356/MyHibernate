package com.example.artem.hibernate.dao;

import com.example.artem.hibernate.HibernateSessionFactoryUtil;
import com.example.artem.hibernate.dto.FeedbackDto;
import com.example.artem.hibernate.dto.ProductInfoDto;
import com.example.artem.hibernate.dto.TopTenPickupPointBySales;
import com.example.artem.hibernate.dto.TopTenProductDto;
import com.example.artem.hibernate.entity.Feedback;
import com.example.artem.hibernate.entity.Product;
import com.example.artem.jdbc.PickupPointDto;
import com.example.artem.jdbc.SaleProductDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.transform.TransformerFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.springframework.cglib.core.Transformer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HibernateDaoImpl implements HibernateDao {

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
                query = session.createQuery(
                    "SELECT p.id, p.name, round(avg(f.assessment), 2) as average" +
                        " from Product p " +
                        "INNER JOIN Feedback f on p.id = f.product.id" +
                        " group by p.id " +
                        "order by round(avg(f.assessment))"
                );
            } else if (parameter.equals("DESC")) {
                query = session.createQuery(
                    "SELECT p.id, p.name, round(avg(f.assessment), 2) as average" +
                        " from Product p" +
                        " INNER JOIN Feedback f on p.id = f.product.id" +
                        " group by p.id " +
                        "order by round(avg(f.assessment)) desc");
            } else {
                log.error("Параметр введен неверно - {}", parameter);
            }
            if (query != null) {
                query.setMaxResults(10);
                query.setResultListTransformer(Transformers.aliasToBean(TopTenProductDto.class));
                transaction.commit();
                return query.getResultList();
            } else {
                transaction.commit();
                return Collections.emptyList();
            }
        } catch (HibernateException e) {
            log.error("Ошибка -  ", e);
            return Collections.emptyList();
        }
    }

    @Override
    public ProductInfoDto getProductAndFeedbacks(int id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();
            Query<Object[]> productAndInfo = session.createNativeQuery(
                "select p.id, p.name, f.id, f.author, f.comment, f.assessment"
                    + " from product p " +
                    "inner join feedback f on p.id = f.product_id " +
                    "where p.id = :productId");

            Query<BigDecimal> selectAverageById = session.createNativeQuery(
                "select round(avg(f.assessment), 2) as average " +
                    "from feedback f " +
                    "where f.product_id = :productId");
            productAndInfo.setParameter("productId", id);
            selectAverageById.setParameter("productId", id);

            List<Object[]> resultListProductAndInfo = productAndInfo.getResultList();
            if (!resultListProductAndInfo.isEmpty()) {
                ProductInfoDto productInfoDto = new ProductInfoDto();
                productInfoDto.setId((Integer) resultListProductAndInfo.get(0)[0]);
                productInfoDto.setName((String) resultListProductAndInfo.get(0)[1]);

                List<FeedbackDto> feedbackList = new ArrayList<>();
                for (Object[] row : resultListProductAndInfo) {
                    FeedbackDto feedbackDto = new FeedbackDto();
                    feedbackDto.setFeedbackId((Integer) row[2]);
                    feedbackDto.setAuthor((String) row[3]);
                    feedbackDto.setComment((String) row[4]);
                    feedbackDto.setAssessment((Integer) row[5]);
                    feedbackDto.setProductId(productInfoDto.getId());
                    feedbackList.add(feedbackDto);
                }
                productInfoDto.setProductList(feedbackList);
                productInfoDto.setAverage(selectAverageById.getSingleResult().doubleValue());
                log.info("{}", productInfoDto);
                transaction.commit();
                return productInfoDto;
            }
            transaction.commit();
            return null;
        }
    }

    @Override
    public boolean saleProduct(SaleProductDto dto) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            Query<Integer> quantity = session.createNativeQuery(
                "select i.quantity " +
                    "from inventory i " +
                    "where product_id = :productId and pickup_point_id = :pickupPointId"
            );
            quantity.setParameter("productId", dto.getProductId());
            quantity.setParameter("pickupPointId", dto.getPickupPointId());
            Integer singleResult = quantity.getSingleResult();
            if (singleResult >= dto.getCount()) {
                Query updateQuantity = session.createNativeQuery(
                    "update inventory set quantity = quantity - :count " +
                        "where product_id = :productId and pickup_point_id = :pickupPointId"
                );
                updateQuantity.setParameter("productId", dto.getProductId());
                updateQuantity.setParameter("count", dto.getCount());
                updateQuantity.setParameter("pickupPointId", dto.getPickupPointId());
                updateQuantity.executeUpdate();

                Product product = session.get(Product.class, dto.getProductId());
                int resultPrice = product.getPrice() * dto.getCount();

                Query insertSaleHistory = session.createNativeQuery(
                    "insert into sales_history(product_id, quantity, price, pickup_point_id) " +
                        "values (:productId, :resultPrice, :price, :pickupPointId)"
                );
                insertSaleHistory.setParameter("productId", dto.getProductId());
                insertSaleHistory.setParameter("quantity", dto.getCount());
                insertSaleHistory.setParameter("resultPrice", resultPrice);
                insertSaleHistory.setParameter("pickupPointId", dto.getPickupPointId());
                insertSaleHistory.executeUpdate();
                transaction.commit();
                return true;
            }
            transaction.commit();
            return false;
        } catch (HibernateException e) {
            if (transaction != null) {
                log.error("Ошибка -  ", e);
                transaction.rollback();
            }
            return false;
        }
    }

    @Override
    public List<TopTenPickupPointBySales> getTopTenPickupPointBySales() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();

            Query<TopTenPickupPointBySales> topTen = session.createNativeQuery(
                "select p.id, p.address, s.sale_date, sum(s.price) as amount " +
                    "from pickup_point p " +
                    "inner join sales_history s on p.id = s.pickup_point_id " +
                    "where date_trunc('month', s.sale_date) = date_trunc('month', current_date)" +
                    "group by p.id, p.address, s.sale_date " +
                    "order by amount desc " +
                    "limit 10"
            );
            topTen.setResultTransformer(Transformers.aliasToBean(TopTenPickupPointBySales.class));
            transaction.commit();
            return topTen.getResultList();
        } catch (HibernateException e) {
            log.error("Ошибка -  ", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<PickupPointDto> getWhereNameExistParameter(String parameter) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.getTransaction();
            transaction.begin();

            Query<PickupPointDto> selectByParameter = session.createNativeQuery(
                "select p.id, p.address, p.opening_hours, p.closed_hours, pr.name from pickup_point p " +
                    "inner join inventory i on p.id = i.pickup_point_id " +
                    "inner join product pr on pr.id = i.product_id " +
                    "where pr.name ilike :parameter"
            );
            selectByParameter.setParameter("parameter", "%" + parameter + "%");
            selectByParameter.setResultListTransformer(Transformers.aliasToBean(PickupPointDto.class));
            transaction.commit();
            return selectByParameter.getResultList();
        } catch (HibernateException e) {
            log.error("Ошибка -  ", e);
            return Collections.emptyList();
        }
    }
}
