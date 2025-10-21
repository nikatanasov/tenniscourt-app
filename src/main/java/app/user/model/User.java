package app.user.model;

import app.cart.model.Cart;
import app.reservation.model.Reservation;
import app.transaction.model.Transaction;
import app.wallet.model.Wallet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"users\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    private String firstName;

    private String lastName;

    private String profilePicture;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "user")
    private Cart cart;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "owner")
    private Wallet wallet;

    //@OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
    //private List<Transaction> transactions;

    //@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    //private List<Reservation> reservations;
}
