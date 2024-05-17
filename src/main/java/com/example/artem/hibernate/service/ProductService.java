package com.example.artem.hibernate.service;

import com.example.artem.hibernate.dao.ProductDao;
import com.example.artem.hibernate.entity.Product;
import com.example.artem.jdbc.ProductDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductDao productDao;

    public ProductDto getProduct(int id) {
        Product product = productDao.getProductById(id);
        ProductDto dto = new ProductDto();
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setProductPrice(product.getPrice());
        return dto;
    }
}
