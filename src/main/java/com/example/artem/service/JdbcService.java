package com.example.artem.service;

import java.sql.SQLException;
import java.util.List;

public interface JdbcService {
    boolean createTable();

    boolean addProduct(AddProductDto dto);

    boolean getProduct(GetProductDto dto);

    List<String> getAllPickupPoint();

    List<String> getPickupPointWithAllProduct(int pickupPointId);

    List<String> getAllPickupPointByProductId(int productId);
}
