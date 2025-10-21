package app.scheduler;

import app.reservation.model.Reservation;
import app.reservation.model.ReservationStatus;
import app.reservation.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
public class ReservationStatusScheduler {
    private  final ReservationService reservationService;

    @Autowired
    public ReservationStatusScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(fixedDelay = 20000)
    @Transactional
    public void updateReservationStatus(){
        List<Reservation> completedReservations = reservationService.getCompletedReservations();
        if(completedReservations.isEmpty()){
            log.info("No completed reservations found!");
        }

        for(Reservation reservation:completedReservations){
            if(reservation.getStatus() != ReservationStatus.COMPLETED) {
                reservation.setStatus(ReservationStatus.COMPLETED);
                reservationService.collectCompletedReservation(reservation);
                log.info("Reservation status for reservation with id "+reservation.getId()+" is set to completed!");
            }
        }
    }
}
