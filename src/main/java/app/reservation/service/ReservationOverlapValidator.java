package app.reservation.service;

import app.exceptions.ReservationOverlapException;
import app.reservation.model.Reservation;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationOverlapValidator {
    private final TransactionService transactionService;

    @Autowired
    public ReservationOverlapValidator(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void validate(Reservation reservation, List<Reservation> reservations, BigDecimal reservationTotalPrice, User user){
        for(Reservation r:reservations){
            if(reservation.getStartTime().isBefore(r.getEndTime()) && reservation.getEndTime().isAfter(r.getStartTime())){
                transactionService.createTransaction(reservationTotalPrice, TransactionType.RESERVATION_PAYMENT, TransactionStatus.FAILED, "Reservation of court!", "There is reservation at that time", LocalDateTime.now(), user, null);
                throw new ReservationOverlapException("There is reservation at that time");
            }
        }
    }
}
