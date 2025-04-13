package org.sbproject03.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CartItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cartRefId;
    private String productId;
    private int quantity;

    public CartItems(String cartId, String productId, int quantity) {
        this.cartRefId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }
}

