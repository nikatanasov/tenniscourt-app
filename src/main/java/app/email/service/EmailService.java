package app.email.service;

import app.web.dto.ReservationNotificationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @EventListener
    public void createNewEmailWhenReservationHappens(ReservationNotificationEvent event){
        System.out.println("Sending email for successful reservation of court "+event.getCourtName()+" to "+event.getEmail()+"!");
    }
}
