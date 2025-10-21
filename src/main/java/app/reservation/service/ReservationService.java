package app.reservation.service;

import app.court.model.Court;
import app.notification.service.NotificationService;
import app.reservation.model.Reservation;
import app.reservation.model.ReservationStatus;
import app.reservation.repository.ReservationRepository;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.service.WalletService;
import app.web.dto.ReservationNotificationEvent;
import app.web.dto.ReservationRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TransactionService transactionService;
    private final WalletService walletService;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, TransactionService transactionService, WalletService walletService, NotificationService notificationService, ApplicationEventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public boolean createNewReservation(ReservationRequest reservationRequest, Court court, User user) {
        Wallet wallet = user.getWallet();


        LocalDateTime reservationStartTime = reservationRequest.getStartTime();
        LocalDateTime reservationEndTime = reservationRequest.getStartTime().plusHours(reservationRequest.getHoursOfGame());
        //List<Reservation> futureReservationList = this.reservationRepository.findAll().stream().filter(r->r.getCourt().getName().equals(court.getName())).collect(Collectors.toList());
        List<Reservation> reservations = this.reservationRepository.findAllByCourtName(court.getName());

        LocalTime openTime = LocalTime.of(9, 0);
        LocalTime closeTime = LocalTime.of(21, 0);

        if(reservationEndTime.toLocalTime().isAfter(closeTime) || reservationStartTime.toLocalTime().isBefore(openTime) || reservationStartTime.toLocalTime().isAfter(closeTime)){
            return false;
        }

        if(reservationStartTime.toLocalDate().isAfter(LocalDate.now().plusDays(7))){
            return false;
        }

        if(reservationStartTime.isBefore(LocalDateTime.now())){
            return false;
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .court(court)
                .startTime(reservationRequest.getStartTime())
                .endTime(reservationEndTime)
                .totalPrice(court.getPricePerHour().multiply(BigDecimal.valueOf(reservationRequest.getHoursOfGame())))
                .status(ReservationStatus.CONFIRMED)//CONFIRMED
                .build();

        for(Reservation f:reservations){
            if(reservation.getStartTime().isBefore(f.getEndTime()) && reservation.getEndTime().isAfter(f.getStartTime())){
                return false;
            }
        }

        if(wallet.getStatus() == WalletStatus.INACTIVE){
            transactionService.createTransaction(reservation.getTotalPrice(), TransactionType.RESERVATION_PAYMENT, TransactionStatus.FAILED, "Reservation of court!", "Inactive wallet status!", LocalDateTime.now(), user, null);
            return false;
        }

        if(reservation.getTotalPrice().compareTo(wallet.getBalance()) > 0){
            transactionService.createTransaction(reservation.getTotalPrice(), TransactionType.RESERVATION_PAYMENT, TransactionStatus.FAILED, "Reservation of court!", "Balance of wallet is less than reservation amount!", LocalDateTime.now(), user, null);
            return false;
        }

        wallet.setBalance(wallet.getBalance().subtract(reservation.getTotalPrice()));
        wallet.setUpdatedOn(LocalDateTime.now());
        walletService.collectWallet(wallet);
        reservationRepository.save(reservation);

        notificationService.sendNotification(reservation.getUser().getId(), "Reservation of court "+reservation.getCourt().getName()+"!", "Successful reservation of court "+reservation.getCourt().getName()+" for user with email "+reservation.getUser().getEmail()+"!", "RESERVATION");

        ReservationNotificationEvent event = ReservationNotificationEvent.builder()
                .userId(reservation.getUser().getId())
                .email(reservation.getUser().getEmail())
                .totalPrice(reservation.getTotalPrice())
                .courtName(reservation.getCourt().getName())
                .build();

        eventPublisher.publishEvent(event);

        transactionService.createTransaction(reservation.getTotalPrice(), TransactionType.RESERVATION_PAYMENT, TransactionStatus.SUCCEEDED, "Successful reservation of court "+reservation.getCourt().getName()+" for "+reservationRequest.getHoursOfGame()+" hours!", null, LocalDateTime.now(), reservation.getUser(), null);
        return true;
    }

    public List<Reservation> getReservationsForToday(Court court) {
        LocalDate today = LocalDate.now();
        LocalDateTime startLimit = today.atTime(9, 0);
        LocalDateTime endLimit = today.atTime(21, 0);

        return reservationRepository.findByCourtAndStartTimeBetween(court, startLimit ,endLimit)
                .stream().filter(r -> r.getEndTime().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public Map<LocalDate, List<Reservation>> getReservationsForNextSevenDays(Court court) {
        LocalDateTime firstDay = LocalDateTime.now();
        LocalDateTime lastDay = firstDay.plusDays(7);
        List<Reservation> reservations = reservationRepository.findByCourtAndStartTimeBetween(court, firstDay, lastDay).stream().filter(r -> r.getEndTime().isAfter(LocalDateTime.now())).collect(Collectors.toList());
        return reservations.stream().collect(Collectors.groupingBy(r ->r.getStartTime().toLocalDate()));
    }

    public List<Reservation> getCompletedReservations() {
        return reservationRepository.findAll().stream().filter(r->r.getEndTime().isBefore(LocalDateTime.now())).collect(Collectors.toList());
    }

    public void collectCompletedReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservationForUser(User user) {
        return reservationRepository.findAllByUserId(user.getId());
    }
}
