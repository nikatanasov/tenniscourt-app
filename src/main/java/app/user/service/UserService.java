package app.user.service;

import app.cart.model.Cart;
import app.cart.service.CartService;
import app.exceptions.UsernameAlreadyExistException;
import app.notification.dto.NotificationPreference;
import app.notification.service.NotificationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import app.web.dto.EditProfileRequest;

import app.web.dto.RegisterRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;
    private final CartService cartService;
    private final NotificationService notificationService;

    @Autowired
    public UserService(UserRepository userRepository, WalletService walletService, PasswordEncoder passwordEncoder, CartService cartService, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletService = walletService;
        this.cartService = cartService;
        this.notificationService = notificationService;
    }


    @Transactional
    public User registerUser(RegisterRequest registerRequest){
        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());
        if(userOptional.isPresent()){
            throw new UsernameAlreadyExistException("The user is already registered!");
        }

        User user = userRepository.save(createUser(registerRequest));
        Wallet wallet = walletService.createWallet(user);
        user.setWallet(wallet);
        Cart cart = cartService.createCart(user);
        user.setCart(cart);

        notificationService.saveNotificationPreference(user.getId(), false, false, user.getEmail());
        log.info("Successfully created new account for user "+user.getUsername());
        return user;
    }



    public User createUser(RegisterRequest registerRequest){
        return User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(()->new RuntimeException("User with id "+userId+" does not exist!"));
    }

    public void editProfile(User user, EditProfileRequest editProfileRequest) {
        user.setUsername(editProfileRequest.getUsername());
        user.setFirstName(editProfileRequest.getFirstName());
        user.setLastName(editProfileRequest.getLastName());
        user.setEmail(editProfileRequest.getEmail());
        user.setProfilePicture(editProfileRequest.getProfilePicture());

        if(editProfileRequest.getPassword() != null && !editProfileRequest.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(editProfileRequest.getPassword()));
        }

        userRepository.save(user);

        NotificationPreference notificationPreference = notificationService.getNotificationPreferencePage(user.getId());
        notificationService.saveNotificationPreference(user.getId(), notificationPreference.isReservationEnabled(), notificationPreference.isProductEnabled(), user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("User with this username does not exist!"));
        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }
}
