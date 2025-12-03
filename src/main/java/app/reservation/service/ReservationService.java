package app.reservation.service;

import app.court.model.Court;
import app.exceptions.InsufficientFundsException;
import app.exceptions.ReservationOverlapException;
import app.exceptions.ReservationTimeException;
import app.exceptions.WalletInactiveException;
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
    private final ReservationTimeValidator reservationTimeValidator;
    private final ReservationPaymentProcessor reservationPaymentProcessor;
    private final ReservationOverlapValidator reservationOverlapValidator;
    private final ReservationNotificationPublisher reservationNotificationPublisher;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, TransactionService transactionService, WalletService walletService, NotificationService notificationService, ApplicationEventPublisher eventPublisher, ReservationTimeValidator reservationTimeValidator, ReservationPaymentProcessor reservationPaymentProcessor, ReservationOverlapValidator reservationOverlapValidator, ReservationNotificationPublisher reservationNotificationPublisher) {
        this.reservationRepository = reservationRepository;
        this.transactionService = transactionService;
        this.walletService = walletService;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
        this.reservationTimeValidator = reservationTimeValidator;
        this.reservationPaymentProcessor = reservationPaymentProcessor;
        this.reservationOverlapValidator = reservationOverlapValidator;
        this.reservationNotificationPublisher = reservationNotificationPublisher;
    }

    @Transactional
    public Reservation createNewReservation(ReservationRequest reservationRequest, Court court, User user) {
        Wallet wallet = user.getWallet();

        BigDecimal reservationTotalPrice = court.getPricePerHour().multiply(BigDecimal.valueOf(reservationRequest.getHoursOfGame()));
        LocalDateTime reservationStartTime = reservationRequest.getStartTime();
        LocalDateTime reservationEndTime = reservationRequest.getStartTime().plusHours(reservationRequest.getHoursOfGame());
        List<Reservation> reservations = this.reservationRepository.findAllByCourtName(court.getName());

        reservationTimeValidator.validate(reservationStartTime, reservationEndTime, reservationTotalPrice, user);

        Reservation reservation = Reservation.builder()
                .user(user)
                .court(court)
                .startTime(reservationRequest.getStartTime())
                .endTime(reservationEndTime)
                .totalPrice(reservationTotalPrice)
                .status(ReservationStatus.CONFIRMED)
                .build();

        reservationOverlapValidator.validate(reservation, reservations, reservationTotalPrice, user);

        reservationPaymentProcessor.validate(wallet, reservation, user);

        Reservation savedReservation = reservationRepository.save(reservation);

        reservationNotificationPublisher.send(reservation);

        transactionService.createTransaction(reservation.getTotalPrice(), TransactionType.RESERVATION_PAYMENT, TransactionStatus.SUCCEEDED, "Successful reservation of court "+reservation.getCourt().getName()+" for "+reservationRequest.getHoursOfGame()+" hours!", null, LocalDateTime.now(), reservation.getUser(), null);
        return savedReservation;
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

    public List<Reservation> getAllReservationsByCourtName(String courtName) {
        return reservationRepository.findAllByCourtName(courtName);
    }
}
