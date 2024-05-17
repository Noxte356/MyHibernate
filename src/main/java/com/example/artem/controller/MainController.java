package com.example.artem.controller;

import com.example.artem.hibernate.dao.ProductDao;
import com.example.artem.hibernate.dto.FeedbackDto;
import com.example.artem.hibernate.dto.TopTenProductDto;
import com.example.artem.hibernate.entity.Product;
import com.example.artem.hibernate.service.FeedbackService;
import com.example.artem.hibernate.service.ProductService;
import com.example.artem.jdbc.*;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artem")
public class MainController {

    private final JdbcService jdbcService;
    private final ProductService productService;
    private final FeedbackService feedbackService;

    @GetMapping("/get/{id}")
    public ProductDto getProductById(@PathVariable int id) {
        return productService.getProduct(id);
    }

    @GetMapping("/getTopTen/{parameter}")
    public List<TopTenProductDto> getTopTen(@PathVariable String parameter){
        List<TopTenProductDto> topTen = feedbackService.getTopTen(parameter);
        return topTen;
    }

    @PostMapping("/createFeedback")
    public FeedbackDto createFeedback(@RequestBody FeedbackDto dto){
        return feedbackService.create(dto);
    }

    @GetMapping("/")
    public ResponseEntity<Boolean> createTables() {
        boolean bool = jdbcService.createTable();
        return ResponseEntity.ok(bool);
    }

    @GetMapping("get-all-pickupPoint")
    public ResponseEntity<List<PickupPointDto>> getAllPickupPoint() {
        return ResponseEntity.ok(jdbcService.getAllPickupPoint());
    }

    @GetMapping("/{pickupPointId}")
    public ResponseEntity<ResultPickupPointWithAllProductDto> getAllProductById(@PathVariable() Integer pickupPointId) {
        return ResponseEntity.ok(jdbcService.getPickupPointWithAllProduct(pickupPointId));
    }

    @GetMapping("getAllPickupPointByProductId/{productId}")
    public ResponseEntity<List<PickupPointDto>> getAllPickupPointByProductId(@PathVariable() Integer productId) {
        return ResponseEntity.ok(jdbcService.getAllPickupPointByProductId(productId));
    }

    @GetMapping("getAllPickupPointByTime/{time}")
    public ResponseEntity<List<PickupPointDto>> getAllPickupPointByTime(@PathVariable() Time time) {
        return ResponseEntity.ok(jdbcService.getAllPickupPointByTime(time));
    }

    @PostMapping("add-product")
    public ResponseEntity<Boolean> addProduct(@RequestBody AddProductDto dto) {
        boolean bool = jdbcService.addProduct(dto);
        return ResponseEntity.ok(bool);
    }

    @PostMapping("get-product")
    public ResponseEntity<Boolean> getProduct(@RequestBody GetProductDto dto) {
        boolean bool = jdbcService.buyProduct(dto);
        return ResponseEntity.ok(bool);
    }
}
