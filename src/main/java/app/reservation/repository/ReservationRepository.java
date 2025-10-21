package app.reservation.repository;

import app.court.model.Court;
import app.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByCourtAndStartTimeBetween(Court court, LocalDateTime startTime, LocalDateTime endTime);

    List<Reservation> findAllByUserId(UUID userId);

    List<Reservation> findAllByCourtName(String courtName);
}
