package app.web.dto;

import app.product.model.ProductCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddNewProductRequest {

    @Size(min = 3, message = "Product name must be at least 3 symbols!")
    private String name;

    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be a positive value")
    private BigDecimal price;

    @NotNull(message = "Quantity must not be null")
    @Positive(message = "Quantity must be a positive value")
    private int quantity;

    @URL(message = "Invalid imageUrl!")
    private String imageUrl;

    @NotNull
    private ProductCategory productCategory;
}
