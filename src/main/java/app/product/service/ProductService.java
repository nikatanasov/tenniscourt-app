package app.product.service;

import app.product.model.Product;
import app.product.repository.ProductRepository;
import app.web.dto.AddNewProductRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    public void addNewProduct(AddNewProductRequest addNewProductRequest) {
        Product product = Product.builder()
                .name(addNewProductRequest.getName())
                .quantity(addNewProductRequest.getQuantity())
                .price(addNewProductRequest.getPrice())
                .productCategory(addNewProductRequest.getProductCategory())
                .imageUrl(addNewProductRequest.getImageUrl())
                .addedOn(LocalDateTime.now())
                .description(null)
                .build();
        productRepository.save(product);
    }


    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("There is no product with id "+id+"!"));
    }

    public void reduceProductQuantity(Product product) {
        if(product.getQuantity() - 1 < 0){
            return;
        }//ЩЕ НАПРАВЯ SCHEDULER ЗА ДА СЕ МАХНАТ ИЗЧЕРПАНИТЕ ПРОДУКТИ

        product.setQuantity(product.getQuantity() - 1);
        productRepository.save(product);
    }

    public void upProductQuantity(Product product) {
        product.setQuantity(product.getQuantity() + 1);
        productRepository.save(product);
    }

    public void removeProduct(Product product) {
        productRepository.delete(product);
    }

    public void updateProductsDescription() {
        List<Product> products = getAllProducts();
        for(Product product:products){
            if(product.getQuantity() == 0){
                product.setDescription("Наличност : ИЗЧЕРПАН");
                productRepository.save(product);
            }
        }
    }
}
