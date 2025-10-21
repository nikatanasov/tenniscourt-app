package app.cart;

import app.cart.model.Cart;
import app.cart.model.CartItem;
import app.cart.repository.CartItemRepository;
import app.cart.repository.CartRepository;
import app.cart.service.CartService;
import app.product.model.Product;
import app.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static app.TestBuilder.aRandomUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceUTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void removeProductFromCart_withItemQuantityOver1(){
        User user = aRandomUser();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Water")
                .quantity(10)
                .build();

        CartItem cartItem1 = CartItem.builder()
                .quantity(3)
                .product(product)
                .build();

        /*CartItem cartItem2 = CartItem.builder()
                .quantity(0)
                .product(product)
                .build();*/

        Cart cart = Cart.builder()
                .user(user)
                .items(List.of(cartItem1))
                .build();

        user.setCart(cart);

        cartService.removeProductFromCart(product, user);

        assertEquals(2, cartItem1.getQuantity());

        verify(cartItemRepository, times(1)).save(cartItem1);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void removeProductFromCart_withItemQuantityEquals0(){
        User user = aRandomUser();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Water")
                .quantity(10)
                .build();

        CartItem cartItem1 = CartItem.builder()
                .quantity(0)
                .product(product)
                .build();

        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>(List.of(cartItem1)))
                .build();

        user.setCart(cart);


        cartService.removeProductFromCart(product, user);

        assertEquals(0 ,user.getCart().getItems().size());

        verify(cartItemRepository, times(1)).delete(cartItem1);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void addProductToCart_withProductAlreadyInCart(){
        User user = aRandomUser();

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Water")
                .quantity(10)
                .build();

        CartItem cartItem1 = CartItem.builder()
                .quantity(3)
                .product(product)
                .build();

        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>(List.of(cartItem1)))
                .build();

        user.setCart(cart);

        cartService.addProductToCart(product, user);
        assertEquals(4, cartItem1.getQuantity());

        verify(cartItemRepository, times(1)).save(cartItem1);
        verify(cartRepository, times(1)).save(cart);
    }

}
