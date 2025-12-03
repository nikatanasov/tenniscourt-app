package app.reservation.service;

import app.exceptions.InsufficientFundsException;
import app.exceptions.WalletInactiveException;
import app.reservation.model.Reservation;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationPaymentProcessor {
    private final WalletService walletService;
    private final TransactionService transactionService;

    @Autowired
    public ReservationPaymentProcessor(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    public void validate(Wallet wallet, Reservation reservation, User user) {
        if (wallet.getStatus() == WalletStatus.INACTIVE) {
            transactionService.createTransaction(reservation.getTotalPrice(), TransactionType.RESERVATION_PAYMENT, TransactionStatus.FAILED, "Reservation of court!", "Inactive wallet status!", LocalDateTime.now(), user, null);
            throw new WalletInactiveException("Wallet status is INACTIVE!");
        }

        if (reservation.getTotalPrice().compareTo(wallet.getBalance()) > 0) {
            transactionService.createTransaction(reservation.getTotalPrice(), TransactionType.RESERVATION_PAYMENT, TransactionStatus.FAILED, "Reservation of court!", "Balance of wallet is less than reservation amount!", LocalDateTime.now(), user, null);
            throw new InsufficientFundsException("Insufficient balance!");
        }

        wallet.setBalance(wallet.getBalance().subtract(reservation.getTotalPrice()));
        wallet.setUpdatedOn(LocalDateTime.now());
        walletService.collectWallet(wallet);
    }
}
