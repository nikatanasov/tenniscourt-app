package app;

import app.cart.model.CartItem;
import app.cart.service.CartService;
import app.product.model.Product;
import app.product.model.ProductCategory;
import app.product.service.ProductService;
import app.transaction.model.Transaction;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.service.WalletService;
import app.web.dto.AddNewProductRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class WalletITest {
    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    @Test
    void buyProducts_happyPath(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();

        User user = userService.registerUser(registerRequest);

        AddNewProductRequest addNewProductRequest = AddNewProductRequest.builder()
                .name("Water")
                .price(BigDecimal.valueOf(2))
                .quantity(3)
                .imageUrl("https://img.waterworld.com/files/base/ebm/ww/image/2024/03/65e724a81f04ab001e1d8544-dreamstime_xxl_23298413.png?auto=format,compress&fit=fill&fill=blur&w=1200&h=630")
                .productCategory(ProductCategory.WATER)
                .build();

        productService.addNewProduct(addNewProductRequest);

        cartService.addProductToCart(productService.getAllProducts().get(0), user);

        Transaction transaction = walletService.buyProducts(user.getCart().getItems(), user);
        assertEquals(transaction.getOwner().getId(), user.getId());
        assertThat(transaction.getProducts()).hasSize(1);
        assertEquals(transaction.getAmount(), productService.getAllProducts().get(0).getPrice());
    }

    @Test
    void buyProductsWithEmptyItems_throwsException(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();

        User user = userService.registerUser(registerRequest);

        assertThrows(RuntimeException.class, () -> walletService.buyProducts(user.getCart().getItems(), user));
    }
}
