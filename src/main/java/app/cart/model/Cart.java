package app.cart.model;

import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private User user;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "cart")
    private List<CartItem> items = new ArrayList<>();//Builder пренебрегва създаването на списък и трябва да го създадем в сървиса
}
