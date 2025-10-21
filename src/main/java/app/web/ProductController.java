package app.web;

import app.cart.model.CartItem;
import app.cart.service.CartService;
import app.product.model.Product;
import app.product.service.ProductService;
import app.security.AuthenticationMetadata;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.service.WalletService;
import app.web.dto.AddNewProductRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final UserService userService;
    private final ProductService productService;
    private final TransactionService transactionService;
    private final CartService cartService;
    private final WalletService walletService;

    @Autowired
    public ProductController(UserService userService, ProductService productService, TransactionService transactionService, CartService cartService, WalletService walletService) {
        this.userService = userService;
        this.productService = productService;
        this.transactionService = transactionService;
        this.cartService = cartService;
        this.walletService = walletService;
    }


    @GetMapping
    public ModelAndView getAllProducts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Product> products = productService.getAllProducts();
        List<CartItem> items = user.getCart().getItems();
        modelAndView.setViewName("products-list");
        modelAndView.addObject("products", products);
        modelAndView.addObject("items", items);
        return modelAndView;
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddProductsPageForAdmins(AddNewProductRequest addNewProductRequest, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getById(authenticationMetadata.getUserId());
        modelAndView.setViewName("product-add");
        modelAndView.addObject("addNewProductRequest", addNewProductRequest);
        return modelAndView;
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView processAddingNewProductForAdmin(@Valid AddNewProductRequest addNewProductRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("addNewProductRequest", addNewProductRequest);
            modelAndView.setViewName("product-add");
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getById(authenticationMetadata.getUserId());
        productService.addNewProduct(addNewProductRequest);
        return new ModelAndView("redirect:/home");
    }

    @PostMapping("/cart/{id}")
    public String processAddToCart(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        //ModelAndView modelAndView = new ModelAndView();
        User user = userService.getById(authenticationMetadata.getUserId());
        Product product = productService.getProductById(id);
        cartService.addProductToCart(product, user);
        productService.reduceProductQuantity(product);
        return "redirect:/products";
    }

    //@PostMapping("/cart/remove/{id}")
    @DeleteMapping("/cart/{id}")
    public String removeProductFromCart(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        Product product = productService.getProductById(id);
        cartService.removeProductFromCart(product, user);
        productService.upProductQuantity(product);
        return "redirect:/products";
    }

    @PostMapping("/checkout")
    public String processBuyProducts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        List<CartItem> items = user.getCart().getItems();
        walletService.buyProducts(items, user);
        productService.updateProductsDescription();
        return "redirect:/products";
    }
}
