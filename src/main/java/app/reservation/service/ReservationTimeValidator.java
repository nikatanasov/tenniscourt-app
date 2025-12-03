package app.reservation.service;

import app.exceptions.ReservationTimeException;
import app.reservation.model.Reservation;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.web.dto.ReservationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class ReservationTimeValidator {
    private final TransactionService transactionService;

    @Autowired
    public ReservationTimeValidator(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void validate(LocalDateTime reservationStartTime, LocalDateTime reservationEndTime, BigDecimal reservationTotalPrice, User user){
        LocalTime open = LocalTime.of(9, 0);
        LocalTime close = LocalTime.of(21, 0);

        if(reservationStartTime.toLocalTime().isBefore(open) || reservationEndTime.toLocalTime().isAfter(close)){
            transactionService.createTransaction(reservationTotalPrice, TransactionType.RESERVATION_PAYMENT, TransactionStatus.FAILED, "Reservation of court!", "Reservation is outside working hours!", LocalDateTime.now(), user, null);
            throw new ReservationTimeException("Reservation is outside working hours!");
        }

        if(reservationStartTime.toLocalDate().isAfter(LocalDate.now().plusDays(7))){
            transactionService.createTransaction(reservationTotalPrice, TransactionType.RESERVATION_PAYMENT, TransactionStatus.FAILED, "Reservation of court!", "Reservations are allowed up to 7 days ahead!", LocalDateTime.now(), user, null);
            throw new ReservationTimeException("Reservations are allowed up to 7 days ahead!");
        }

        if(reservationStartTime.isBefore(LocalDateTime.now())){
            transactionService.createTransaction(reservationTotalPrice, TransactionType.RESERVATION_PAYMENT, TransactionStatus.FAILED, "Reservation of court!", "Reservation cannot be in the past!", LocalDateTime.now(), user, null);
            throw new ReservationTimeException("Reservation cannot be in the past!");
        }
    }
}
