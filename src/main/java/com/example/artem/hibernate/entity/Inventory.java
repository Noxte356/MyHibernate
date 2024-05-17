package com.example.artem.hibernate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    @Id
    @Column(name = "product_id")
    private int productId;

    @Id
    @Column(name = "pickup_point_id")
    private int pickupPointId;

    @Column(name = "quantity")
    private int quantity;
}
