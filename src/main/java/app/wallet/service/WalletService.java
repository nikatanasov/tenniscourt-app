package app.wallet.service;

import app.cart.model.CartItem;
import app.cart.service.CartService;
import app.notification.service.NotificationService;
import app.product.model.Product;
import app.product.service.ProductService;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;
    private final CartService cartService;
    private final ProductService productService;
    private final NotificationService notificationService;

    @Autowired
    public WalletService(WalletRepository walletRepository, TransactionService transactionService, CartService cartService, ProductService productService, NotificationService notificationService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
        this.cartService = cartService;
        this.productService = productService;
        this.notificationService = notificationService;
    }

    public Wallet createWallet(User user){
        Wallet wallet = Wallet.builder()
                .owner(user)
                .status(WalletStatus.ACTIVE)
                .balance(BigDecimal.valueOf(30))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        return walletRepository.save(wallet);
    }

    @Transactional
    public Transaction buyProducts(List<CartItem> items, User user) {
        Wallet wallet = user.getWallet();
        if(items.isEmpty()){
            throw new RuntimeException("Cannot create transaction with empty cart");
        }

        BigDecimal price = BigDecimal.ZERO;
        List<Product> productList = new ArrayList<>();
        for(CartItem item:items){
            Product product = productService.getProductById(item.getProduct().getId());
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            price = price.add(itemTotal);
            productList.add(product);
        }

        if(wallet.getStatus() == WalletStatus.INACTIVE){
            return transactionService.createTransaction(price, TransactionType.PRODUCT_PURCHASE, TransactionStatus.FAILED, "Buying products!", "Inactive wallet status!", LocalDateTime.now(), user, productList);
        }

        BigDecimal result = wallet.getBalance().subtract(price);
        if(result.compareTo(BigDecimal.ZERO) < 0){
            return transactionService.createTransaction(price, TransactionType.PRODUCT_PURCHASE, TransactionStatus.FAILED, "Buying products!", "Price of products is bigger than wallet balance!", LocalDateTime.now(), user, productList);
        }

        wallet.setBalance(wallet.getBalance().subtract(price));
        wallet.setUpdatedOn(LocalDateTime.now());

        notificationService.sendNotification(user.getId(), "Buying products!", "You bought products for " + price + "!", "BUYING_PRODUCT");

        cartService.clearCart(user);

        return transactionService.createTransaction(price, TransactionType.PRODUCT_PURCHASE, TransactionStatus.SUCCEEDED, "Buying products!", null, LocalDateTime.now(), user, productList);
    }

    public void addMoney(User user) {
        Wallet wallet = null;
        if (walletRepository.findByOwnerUsername(user.getUsername()).isPresent()) {
            wallet = walletRepository.findByOwnerUsername(user.getUsername()).get();
        }else{
            throw new RuntimeException("There is no wallet for user with username "+user.getUsername()+"!");
        }

        if(wallet.getStatus() == WalletStatus.INACTIVE){
            throw new RuntimeException("Wallet status is INACTIVE!");
        }

        wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(30)));
        wallet.setUpdatedOn(LocalDateTime.now());
        walletRepository.save(wallet);
    }

    public void collectWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }
}
