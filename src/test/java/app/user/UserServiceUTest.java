package app.user;

import app.cart.model.Cart;
import app.cart.service.CartService;
import app.exceptions.UsernameAlreadyExistException;
import app.notification.dto.NotificationPreference;
import app.notification.service.NotificationService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.service.WalletService;
import app.web.dto.EditProfileRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static app.TestBuilder.aRandomUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WalletService walletService;

    @Mock
    private CartService cartService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegisterUser_happyPath(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@abv.bg")
                .password("123123")
                .confirmPassword("123123")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("Vik123")
                .email("viktor@abv.bg")
                .password("123123")
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .status(WalletStatus.ACTIVE)
                .balance(BigDecimal.valueOf(100))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .user(user)
                .items(new ArrayList<>())
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);
        when(walletService.createWallet(any())).thenReturn(wallet);
        when(cartService.createCart(any())).thenReturn(cart);

        User result = userService.registerUser(registerRequest);
        assertNotNull(result);
        assertEquals("Vik123", result.getUsername());
        assertEquals("viktor@abv.bg", result.getEmail());
        assertNotNull(result.getWallet());
        assertNotNull(result.getCart());

        verify(userRepository, times(1)).findByUsername(any());
        verify(userRepository, times(1)).save(any());
        verify(walletService, times(1)).createWallet(any());
        verify(cartService, times(1)).createCart(any());
        verify(notificationService, times(1)).saveNotificationPreference(any(UUID.class), anyBoolean(), anyBoolean(), anyString());
    }

    @Test
    void testRegisterUser_whenUserExist(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@abv.bg")
                .password("123123")
                .confirmPassword("123123")
                .build();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(aRandomUser()));
        assertThrows(UsernameAlreadyExistException.class, () -> userService.registerUser(registerRequest));
        verify(userRepository, never()).save(any());
        verify(walletService, never()).createWallet(any());
        verify(cartService, never()).createCart(any());
        verify(notificationService, never()).saveNotificationPreference(any(UUID.class), anyBoolean(), anyBoolean(), anyString());
    }

    @Test
    void testEditProfile_happyPath(){
        User user = aRandomUser();

        EditProfileRequest editProfileRequest = EditProfileRequest.builder()
                .firstName("Ivan")
                .lastName("Atanasov")
                .username("ivan123")
                .email("ivan123@abv.bg")
                .profilePicture("https://www.nhm.ac.uk/content/dam/nhm-www/discover/human-evolution/homo-sapien-model-close-up-hero.jpg.thumb.1160.1160.png")
                .password("123123")
                .build();

        NotificationPreference preference = NotificationPreference.builder()
                .reservationEnabled(true)
                .productEnabled(true)
                .contactInfo("ivan123@abv.bg")
                .build();

        when(notificationService.getNotificationPreferencePage(any(UUID.class))).thenReturn(preference);
        userService.editProfile(user, editProfileRequest);
        assertEquals(editProfileRequest.getFirstName(), user.getFirstName());
        assertEquals(editProfileRequest.getUsername(), user.getUsername());
        assertEquals(editProfileRequest.getProfilePicture(), user.getProfilePicture());
        verify(notificationService, times(1)).getNotificationPreferencePage(any(UUID.class));
        verify(notificationService, times(1)).saveNotificationPreference(any(UUID.class), anyBoolean(), anyBoolean(), anyString());
    }
}
