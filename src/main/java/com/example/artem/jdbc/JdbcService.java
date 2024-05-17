package com.example.artem.jdbc;

import java.sql.Time;
import java.util.List;

public interface JdbcService {
    boolean createTable();

    boolean addProduct(AddProductDto dto);

    boolean buyProduct(GetProductDto dto);

    List<PickupPointDto> getAllPickupPoint();

    ResultPickupPointWithAllProductDto getPickupPointWithAllProduct(int pickupPointId);

    List<PickupPointDto> getAllPickupPointByProductId(int productId);

    List<PickupPointDto> getAllPickupPointByTime(Time time);
}
