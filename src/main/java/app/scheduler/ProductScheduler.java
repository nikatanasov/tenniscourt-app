package app.scheduler;

import app.product.model.Product;
import app.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class ProductScheduler {
    private final ProductService productService;

    @Autowired
    public ProductScheduler(ProductService productService) {
        this.productService = productService;
    }

    /*@Scheduled(fixedDelay = 10000)
    @Transactional
    public void updateProduct(){
        List<Product> products = productService.getAllProducts();
        for(Product product : products){
            if(product.getQuantity() == 0) {
                log.info("Product with id " + product.getId() + " removed!");
            }
        }
    }*/
}
