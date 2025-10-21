package app.product.model;

import app.cart.model.CartItem;
import app.transaction.model.Transaction;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory productCategory;

    private String imageUrl;

    private LocalDateTime addedOn;

    private String description;

    //@OneToMany(mappedBy = "product")
    //private List<CartItem> cartItems;

    @ManyToMany(mappedBy = "products")
    private List<Transaction> transactions = new ArrayList<>();
}

