package org.example.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

    // primaryKey = orderId + productId
    @EmbeddedId
    private OrderItemId id;

    private Integer quantity;

    private String comment;
}
