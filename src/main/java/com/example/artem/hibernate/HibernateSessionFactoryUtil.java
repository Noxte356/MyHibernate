package com.example.artem.hibernate;

import com.example.artem.hibernate.entity.Feedback;
import com.example.artem.hibernate.entity.Inventory;
import com.example.artem.hibernate.entity.PickupPoint;
import com.example.artem.hibernate.entity.Product;
import com.example.artem.hibernate.entity.SaleHistory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
@Slf4j
public class HibernateSessionFactoryUtil {
    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtil() {}
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Feedback.class);
                configuration.addAnnotatedClass(Inventory.class);
                configuration.addAnnotatedClass(PickupPoint.class);
                configuration.addAnnotatedClass(Product.class);
                configuration.addAnnotatedClass(SaleHistory.class);
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            }catch (Exception e) {
                log.error(e.getMessage());
                log.error("--------------------------");
            }
        }
        return sessionFactory;
    }
}
