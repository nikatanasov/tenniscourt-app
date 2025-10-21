package app.cart.service;

import app.cart.model.Cart;
import app.cart.model.CartItem;
import app.cart.repository.CartItemRepository;
import app.cart.repository.CartRepository;
import app.product.model.Product;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart createCart(User user){
        Cart cart = Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build();
        cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public void addProductToCart(Product product, User user) {
        if(product.getQuantity() == 0){
            return;
        }
        Cart cart = user.getCart();
        List<CartItem> items = cart.getItems();
        for(CartItem item:items){
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                cartItemRepository.save(item);
                cartRepository.save(cart);
                return;
            }
        }
        CartItem newItem = CartItem.builder()
                .product(product)
                .quantity(1)
                .cart(cart)
                .build();
        items.add(newItem);
        cartItemRepository.save(newItem);
        cartRepository.save(cart);
    }

    @Transactional
    public void removeProductFromCart(Product product, User user) {
        Cart cart = user.getCart();
        List<CartItem> items = cart.getItems();
        for(int i = 0; i < items.size(); i++){
            if(items.get(i).getProduct().getId().equals(product.getId())){
                if(items.get(i).getQuantity() > 1){
                    items.get(i).setQuantity(items.get(i).getQuantity() - 1);
                    cartItemRepository.save(items.get(i));
                    cartRepository.save(cart);
                    break;
                }else{
                    cartItemRepository.delete(items.get(i));
                    items.remove(i);
                    cartRepository.save(cart);
                    break;
                }
            }
        }

    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = user.getCart();
        cartItemRepository.deleteAllByCart(cart);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
