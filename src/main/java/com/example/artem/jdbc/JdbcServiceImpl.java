package com.example.artem.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JdbcServiceImpl implements JdbcService {


    @Override
    public boolean createTable() {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement createProduct = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS product (id SERIAL PRIMARY KEY, name VARCHAR(50), price INTEGER)");
            createProduct.execute();

            PreparedStatement createPickupPoint = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS pickup_point " +
                            "(id SERIAL PRIMARY KEY, address VARCHAR(50), opening_hours TIME, closed_hours TIME)");
            createPickupPoint.execute();

            PreparedStatement createInventory = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS inventory " +
                            "(product_id INTEGER NOT NULL, pickup_point_id INTEGER NOT NULL, quantity INTEGER, PRIMARY KEY(product_id, pickup_point_id), FOREIGN KEY(product_id) REFERENCES product(id), FOREIGN KEY(pickup_point_id) REFERENCES pickup_point(id))");
            createInventory.execute();

            PreparedStatement insertPickup = connection.prepareStatement("INSERT INTO pickup_point " +
                    "(address, opening_hours, closed_hours) VALUES ('Красногорск', '09:00', '22:00'), ('Москва', '10:00', '23:00'), ('Санкт-Петербург', '12:00', '23:30')");
            insertPickup.execute();

            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error("Rollback failed", ex);
                }
            }
            log.error("Create table failed", e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    log.error("Failed to close connection", ex);
                }
            }
        }
    }


    @Override
    public boolean addProduct(AddProductDto dto) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            PreparedStatement insertProduct = connection.prepareStatement(
                    "INSERT INTO product (name, price) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            insertProduct.setString(1, dto.getName());
            insertProduct.setInt(2, dto.getPrice());
            insertProduct.execute();

            int primaryKey = 0;
            if (insertProduct.getGeneratedKeys().next()) {
                primaryKey = insertProduct.getGeneratedKeys().getInt(1);
            }

            PreparedStatement insertInventory = connection.prepareStatement(
                    "INSERT INTO inventory (product_id, pickup_point_id, quantity) VALUES (?,?,?)");
            insertInventory.setInt(1, primaryKey);
            insertInventory.setInt(2, dto.getPickupPointId());
            insertInventory.setInt(3, dto.getCount());
            insertInventory.execute();

            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error("Rollback failed", ex);
                }
            }
            log.error("Add product failed", e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    log.error("Failed to close connection", ex);
                }
            }
        }
    }

    @Override
    public boolean buyProduct(GetProductDto dto) {
        try (final Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement selectStatement = connection.prepareStatement("SELECT inventory.quantity FROM inventory WHERE product_id = ? AND pickup_point_id = ?");
            selectStatement.setInt(1, dto.getProductId());
            selectStatement.setInt(2, dto.getPickupPointId());
            selectStatement.execute();
            ResultSet resultSet = selectStatement.getResultSet();
            if (resultSet.next()) {
                int currentQuantity = resultSet.getInt("quantity");
                if (currentQuantity >= dto.getCount()) {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "UPDATE inventory SET quantity = quantity - ? WHERE product_id = ? AND pickup_point_id = ?");
                    preparedStatement.setInt(1, dto.getCount());
                    preparedStatement.setInt(2, dto.getProductId());
                    preparedStatement.setInt(3, dto.getPickupPointId());
                    preparedStatement.execute();
                    connection.commit();
                    return true;
                } else {
                    connection.commit();
                    return false;
                }
            }
            connection.commit();
            return false;

        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<PickupPointDto> getAllPickupPoint() {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            List<PickupPointDto> result = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM pickup_point");
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                PickupPointDto dto = new PickupPointDto();
                String address = resultSet.getString("address");
                int id = resultSet.getInt("id");
                Time openTime = resultSet.getTime("opening_hours");
                Time closeTime = resultSet.getTime("closed_hours");
                dto.setAddress(address);
                dto.setId(id);
                dto.setOpen(openTime);
                dto.setClose(closeTime);
                result.add(dto);
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error("Rollback failed", ex);
                }
            }
            log.error("Get all pickup point failed", e);
            return Collections.emptyList();
        }
    }

    @Override
    public ResultPickupPointWithAllProductDto getPickupPointWithAllProduct(int pickupPointId) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            PreparedStatement selectJoin = connection.prepareStatement(
                    "SELECT pickup_point.address, product.name, inventory.quantity, pickup_point.id, product.id AS productId, product.price" +
                            " FROM pickup_point LEFT JOIN inventory ON inventory.pickup_point_id = pickup_point.id " +
                            "LEFT JOIN product ON inventory.product_id = product.id WHERE pickup_point.id = ?"
            );
            selectJoin.setInt(1, pickupPointId);
            selectJoin.execute();
            ResultSet resultSet = selectJoin.getResultSet();

            ResultPickupPointWithAllProductDto resultDto = new ResultPickupPointWithAllProductDto();
            List<ProductDto> listProductDto = new ArrayList<>();

            while (resultSet.next()) {
                String address = resultSet.getString("address");
                String productName = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                int productId = resultSet.getInt("productId");
                int price = resultSet.getInt("price");
                ProductDto productDto = new ProductDto();
                productDto.setProductId(productId);
                productDto.setProductPrice(price);
                productDto.setProductName(productName);
                productDto.setProductQuantity(quantity);
                listProductDto.add(productDto);

                resultDto.setAddressName(address);
                resultDto.setPickupPointId(pickupPointId);
            }
            resultDto.setInventory(listProductDto);
            connection.commit();
            return resultDto;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error("Rollback failed", ex);
                }
            }
            log.error("Get pickup point failed", e);
            return null;
        }
    }

    @Override
    public List<PickupPointDto> getAllPickupPointByProductId(int productId) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            List<PickupPointDto> result = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT pickup_point.address, pickup_point.id, pickup_point.opening_hours, pickup_point.closed_hours" +
                    " FROM pickup_point INNER JOIN inventory ON inventory.pickup_point_id = pickup_point.id WHERE product_id = ?");
            preparedStatement.setInt(1, productId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                PickupPointDto dto = new PickupPointDto();
                int id = resultSet.getInt("id");
                String address = resultSet.getString("address");
                Time openTime = resultSet.getTime("opening_hours");
                Time closeTime = resultSet.getTime("closed_hours");
                dto.setAddress(address);
                dto.setId(id);
                dto.setOpen(openTime);
                dto.setClose(closeTime);
                result.add(dto);
            }
            connection.commit();
            return result;
        }catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error("Rollback failed", ex);
                }
            }
            log.error("Get all pickup point failed", e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<PickupPointDto> getAllPickupPointByTime(Time time) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            List<PickupPointDto> listPickupPointDto = new ArrayList<>();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from pickup_point WHERE ? BETWEEN opening_hours AND closed_hours");
            preparedStatement.setTime(1, time);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                PickupPointDto pickupPointDto = new PickupPointDto();
                int id = resultSet.getInt("id");
                String address = resultSet.getString("address");
                Time openingHours = resultSet.getTime("opening_hours");
                Time closedHours = resultSet.getTime("closed_hours");
                pickupPointDto.setId(id);
                pickupPointDto.setAddress(address);
                pickupPointDto.setOpen(openingHours);
                pickupPointDto.setClose(closedHours);
                listPickupPointDto.add(pickupPointDto);
            }
            connection.commit();
            return listPickupPointDto;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error("Rollback failed", ex);
                }
            }
            log.error("Get all pickup point failed", e);
            return Collections.emptyList();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection
                (
                        "jdbc:postgresql://localhost:5432/postgres",
                        "user",
                        "user"
                );
    }
}
