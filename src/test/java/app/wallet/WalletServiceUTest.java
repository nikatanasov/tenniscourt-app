package app.wallet;

import app.cart.service.CartService;
import app.notification.service.NotificationService;
import app.product.service.ProductService;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.wallet.repository.WalletRepository;
import app.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static app.TestBuilder.aRandomUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceUTest {
    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private WalletService walletService;

    @Test
    void testAddMoney_happyPath(){
        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.valueOf(20))
                .build();
        when(walletRepository.findByOwnerUsername(any())).thenReturn(Optional.of(wallet));
        walletService.addMoney(User.builder()
                .id(UUID.randomUUID())
                .build());
        assertEquals(BigDecimal.valueOf(50), wallet.getBalance());
        verify(walletRepository, times(1)).save(any());
    }

    @Test
    void testAddMoney_ThrowException(){

        when(walletRepository.findByOwnerUsername(any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> walletService.addMoney(aRandomUser()));
        verify(walletRepository, never()).save(any());
    }
}
