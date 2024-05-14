package com.example.artem.service;

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
        try (final Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement createProduct = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS product " + "(id SERIAL PRIMARY KEY, name VARCHAR(50), price INTEGER)");
            createProduct.execute();

            PreparedStatement createPicupPoint = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS pickup_point " +
                    "(id SERIAL PRIMARY KEY, address VARCHAR(50), opening_hours TIME)");
            createPicupPoint.execute();

            PreparedStatement createInventory = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS inventory " +
                    "(product_id INTEGER NOT NULL, pickup_point_id INTEGER NOT NULL, quantity INTEGER, PRIMARY KEY(product_id, pickup_point_id), FOREIGN KEY(product_id) REFERENCES product(id), FOREIGN KEY(pickup_point_id) REFERENCES pickup_point(id))");
            createInventory.execute();
            PreparedStatement insertPickup = connection.prepareStatement("INSERT INTO pickup_point " +
                                                                             "(address, opening_hours) VALUES ('Красногорск', '09:00'), ('Москва', '10:00'),('Санкт-Петербург', '12:00')");
            insertPickup.execute();
            connection.commit();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean addProduct(AddProductDto dto) {
        try (final Connection connection = getConnection()) {
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
                "INSERT INTO inventory " + "(product_id, pickup_point_id, quantity) VALUES (?,?,?)");
            insertInventory.setInt(1, primaryKey);
            insertInventory.setInt(2, dto.getPickupPointId());
            insertInventory.setInt(3, dto.getCount());
            insertInventory.execute();

            connection.commit();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean getProduct(GetProductDto dto) {
        try (final Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE inventory SET quantity = quantity - ? WHERE product_id = ? AND pickup_point_id = ?");
            preparedStatement.setInt(1, dto.getCount());
            preparedStatement.setInt(2, dto.getProductId());
            preparedStatement.setInt(3, dto.getPickupPointId());
            preparedStatement.execute();
            connection.commit();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public List<String> getAllPickupPoint() {
        try (final Connection connection = getConnection()) {
            List<String> result = new ArrayList<>();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM pickup_point");
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                String address = resultSet.getString("address");
                Time openTime = resultSet.getTime("opening_hours");
                String conc = address + "-" + openTime;
                result.add(conc);
            }
            connection.commit();
            return result;
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getPickupPointWithAllProduct(int pickupPointId) {
        try (final Connection connection = getConnection()) {
            List<String> result = new ArrayList<>();
            connection.setAutoCommit(false);
            PreparedStatement selectJoin = connection.prepareStatement(
                "SELECT pickup_point.address, product.name, inventory.quantity FROM inventory INNER JOIN pickup_point ON inventory.pickup_point_id = pickup_point.id INNER JOIN product ON inventory.product_id = product.id WHERE pickup_point_id = ?"
            );
            selectJoin.setInt(1, pickupPointId);
            selectJoin.execute();
            ResultSet resultSet = selectJoin.getResultSet();
            while (resultSet.next()) {
                String address = resultSet.getString("address");
                String name = resultSet.getString("name");
                int quantity = resultSet.getInt("quantity");
                String conc = "В городе  " + address + " находится " + name + " в количестве " + quantity + " штук";
                result.add(conc);
            }
            connection.commit();
            return result;
        }catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getAllPickupPointByProductId(int productId) {
        try (final Connection connection = getConnection()){
            List<String> result = new ArrayList<>();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT pickup_point.address FROM pickup_point INNER JOIN inventory ON inventory.product_id = ?");
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
