package app.cart.model;


import app.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int quantity;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Product product;
}
