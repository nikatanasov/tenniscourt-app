package app.reservation.service;

import app.notification.service.NotificationService;
import app.reservation.model.Reservation;
import app.web.dto.ReservationNotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ReservationNotificationPublisher {
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ReservationNotificationPublisher(NotificationService notificationService, ApplicationEventPublisher eventPublisher) {
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
    }

    public void send(Reservation reservation){
        notificationService.sendNotification(reservation.getUser().getId(), "Reservation of court "+reservation.getCourt().getName()+"!", "Successful reservation of court "+reservation.getCourt().getName()+" for user with email "+reservation.getUser().getEmail()+"!", "RESERVATION");

        ReservationNotificationEvent event = ReservationNotificationEvent.builder()
                .userId(reservation.getUser().getId())
                .email(reservation.getUser().getEmail())
                .totalPrice(reservation.getTotalPrice())
                .courtName(reservation.getCourt().getName())
                .build();

        eventPublisher.publishEvent(event);
    }
}
