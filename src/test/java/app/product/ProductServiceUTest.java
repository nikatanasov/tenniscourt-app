package app.product;

import app.product.model.Product;
import app.product.repository.ProductRepository;
import app.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void updateProductDescription_happyPath(){
        Product product1 = Product.builder()
                .quantity(0)
                .build();

        Product product2 = Product.builder()
                .quantity(5)
                .description("Water")
                .build();

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));
        productService.updateProductsDescription();
        assertEquals("Наличност : ИЗЧЕРПАН", product1.getDescription());
        assertEquals("Water", product2.getDescription());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void reduceProductQuantity_withQuantityOver0(){
        Product product = Product.builder()
                .quantity(5)
                .build();
        productService.reduceProductQuantity(product);
        assertEquals(4, product.getQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void reduceProductQuantity_withQuantityEqualsTo0(){
        Product product = Product.builder()
                .quantity(0)
                .build();
        productService.reduceProductQuantity(product);
        assertEquals(0, product.getQuantity());
        verify(productRepository, never()).save(any());
    }

    @Test
    void upProductQuantity_happyPath(){
        Product product = Product.builder()
                .quantity(4)
                .build();
        productService.upProductQuantity(product);
        assertEquals(5, product.getQuantity());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void removeProduct_happyPath(){
        Product product = Product.builder()
                .name("Water")
                .quantity(4)
                .build();
        productService.removeProduct(product);
        verify(productRepository, times(1)).delete(product);
    }
}
