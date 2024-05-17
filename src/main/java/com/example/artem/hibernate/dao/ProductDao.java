package com.example.artem.hibernate.dao;

import com.example.artem.hibernate.HibernateSessionFactoryUtil;
import com.example.artem.hibernate.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDao {

    public Product getProductById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Product.class, id);
    }
}
