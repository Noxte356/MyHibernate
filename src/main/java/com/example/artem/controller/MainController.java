package com.example.artem.controller;

import com.example.artem.service.AddProductDto;
import com.example.artem.service.GetProductDto;
import com.example.artem.service.JdbcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artem")
public class MainController {

    private final JdbcService jdbcService;

    @GetMapping("/")
    public ResponseEntity<Boolean> createTables() {
        boolean bool = jdbcService.createTable();
        return ResponseEntity.ok(bool);
    }

    @GetMapping("get-all-pickupPoint")
    public ResponseEntity<String> getAllPickupPoint() {
       return ResponseEntity.ok(jdbcService.getAllPickupPoint().toString());
    }

    @GetMapping("/{pickupPointId}")
    public ResponseEntity<String> getAllProductById(@PathVariable() Integer pickupPointId) {
        return ResponseEntity.ok(jdbcService.getPickupPointWithAllProduct(pickupPointId).toString());
    }

    @PostMapping("add-product")
    public ResponseEntity<Boolean> addProduct(@RequestBody AddProductDto dto) {
        boolean bool = jdbcService.addProduct(dto);
        return ResponseEntity.ok(bool);
    }

    @PostMapping("get-product")
    public ResponseEntity<Boolean> getProduct(@RequestBody GetProductDto dto) {
        boolean bool = jdbcService.getProduct(dto);
        return ResponseEntity.ok(bool);
    }
}
