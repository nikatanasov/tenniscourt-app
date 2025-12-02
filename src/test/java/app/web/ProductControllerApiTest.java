package app.web;

import app.cart.model.CartItem;
import app.cart.service.CartService;
import app.product.model.Product;
import app.product.model.ProductCategory;
import app.product.service.ProductService;
import app.security.AuthenticationMetadata;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.UUID;

import static app.TestBuilder.aRandomUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerApiTest {
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private WalletService walletService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToAllProductsPage_ShouldReturnProductsListPage() throws Exception {
        when(userService.getById(any())).thenReturn(aRandomUser());
        MockHttpServletRequestBuilder request = get("/products")
                .with(user(new AuthenticationMetadata(UUID.randomUUID(), "Vik123", "123123", UserRole.USER, true)));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("products-list"))
                .andExpect(model().attributeExists("products", "items"));

        verify(userService, times(1)).getById(any());
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void postRequestToProcessAddingNewProductForAdmin_happyPath() throws Exception {
        when(userService.getById(any())).thenReturn(aRandomUser());
        MockHttpServletRequestBuilder request = post("/products/new")
                .formField("name", "Water")
                .formField("price", "2.00")
                .formField("quantity", "1")
                .formField("imageUrl", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQWeeOn0l5588g1qOi5xpcityQlvdQc5RYQsA&s")
                .formField("productCategory", "WATER")
                .with(user(new AuthenticationMetadata(UUID.randomUUID(), "Vik123", "123123", UserRole.ADMIN, true)))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService, times(1)).getById(any());
        verify(productService, times(1)).addNewProduct(any());
    }

    @Test
    void getRequestToAddProductsPageForAdmins_ShouldReturnAddProductsPage() throws Exception {
        when(userService.getById(any())).thenReturn(aRandomUser());

        MockHttpServletRequestBuilder request = get("/products/new")
                .with(user(new AuthenticationMetadata(UUID.randomUUID(), "Vik123", "123123", UserRole.ADMIN, true)));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("product-add"))
                .andExpect(model().attributeExists("addNewProductRequest"));

        verify(userService, times(1)).getById(any());
    }

    @Test
    void postRequestToProcessAddToCart_happyPath() throws Exception {
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Water")
                .quantity(10)
                .price(BigDecimal.valueOf(2))
                .productCategory(ProductCategory.WATER)
                .build();

        when(userService.getById(any())).thenReturn(aRandomUser());
        when(productService.getProductById(any())).thenReturn(product);
        MockHttpServletRequestBuilder request = post("/products/cart/{id}", product.getId())
                .with(user(new AuthenticationMetadata(UUID.randomUUID(), "Vik123", "123123", UserRole.USER, true)))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(cartService, times(1)).addProductToCart(any(Product.class), any(User.class));
        verify(productService, times(1)).reduceProductQuantity(any(Product.class));
    }

    @Test
    void postRequestToRemoveProductFromCart_happyPath() throws Exception {
        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Water")
                .quantity(10)
                .price(BigDecimal.valueOf(2))
                .productCategory(ProductCategory.WATER)
                .build();

        when(userService.getById(any())).thenReturn(aRandomUser());
        when(productService.getProductById(any())).thenReturn(product);
        MockHttpServletRequestBuilder request = delete("/products/cart/{id}", product.getId())
                .with(user(new AuthenticationMetadata(UUID.randomUUID(), "Vik123", "123123", UserRole.USER, true)))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(cartService, times(1)).removeProductFromCart(any(Product.class), any(User.class));
        verify(productService, times(1)).upProductQuantity(any(Product.class));
    }

    @Test
    void postRequestToProcessBuyProducts_happyPath() throws Exception {
        when(userService.getById(any())).thenReturn(aRandomUser());

        MockHttpServletRequestBuilder request = post("/products/checkout")
                .with(user(new AuthenticationMetadata(UUID.randomUUID(), "Vik123", "123123", UserRole.USER, true)))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(userService, times(1)).getById(any());
        verify(walletService, times(1)).buyProducts(anyList(), any(User.class));
        verify(productService, times(1)).updateProductsDescription();
    }
}
