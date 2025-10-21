package app.tracking.service;

import app.web.dto.ReservationNotificationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    @EventListener
    public void trackNewReservation(ReservationNotificationEvent event){
        System.out.println("New reservation for user "+event.getUserId()+" happened!");
    }
}
