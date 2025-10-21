package app.cart.repository;

import app.cart.model.Cart;
import app.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    void deleteAllByCart(Cart cart);
}
