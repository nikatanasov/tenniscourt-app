package app;

import app.product.model.Product;
import app.product.model.ProductCategory;
import app.product.service.ProductService;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.repository.TransactionRepository;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddNewProductRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class TransactionITest {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProductService productService;

    @Test
    void testCreateTransaction_happyPath(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();

        User user = userService.registerUser(registerRequest);

        AddNewProductRequest addNewProductRequest1 = AddNewProductRequest.builder()
                .name("Water1")
                .price(BigDecimal.valueOf(1))
                .quantity(3)
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMrGqJ2Is4xMU2ebgeoGhY04xfA6-wCL5xlA&s")
                .productCategory(ProductCategory.WATER)
                .build();

        AddNewProductRequest addNewProductRequest2 = AddNewProductRequest.builder()
                .name("Water2")
                .price(BigDecimal.valueOf(2))
                .quantity(4)
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQMrGqJ2Is4xMU2ebgeoGhY04xfA6-wCL5xlA&s")
                .productCategory(ProductCategory.WATER)
                .build();

        productService.addNewProduct(addNewProductRequest1);
        productService.addNewProduct(addNewProductRequest2);

        Transaction transaction = transactionService.createTransaction(BigDecimal.valueOf(1), TransactionType.DEPOSIT, TransactionStatus.SUCCEEDED, "Buying product!", null, LocalDateTime.now(), user, productService.getAllProducts());
        assertEquals(user, transaction.getOwner());
        assertThat(transaction.getProducts()).hasSize(2);
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
    }
}
