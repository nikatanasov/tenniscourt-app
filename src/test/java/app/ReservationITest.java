package app;

import app.court.model.Court;
import app.court.repository.CourtRepository;
import app.exceptions.ReservationTimeException;
import app.exceptions.WalletInactiveException;
import app.reservation.model.Reservation;
import app.reservation.service.ReservationService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.repository.WalletRepository;
import app.web.dto.RegisterRequest;
import app.web.dto.ReservationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ReservationITest {
    @Autowired
    private UserService userService;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createNewReservation_happyPath() {
        ReservationRequest request = ReservationRequest.builder()
                .startTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(12)
                        .withMinute(0)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        Court court = Court.builder()
                .name("CenterCourt")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://www.edwardssports.co.uk/pub/media/wysiwyg/tennis_court_dimensions_1_.jpg")
                .build();
        courtRepository.save(court);

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();
        User user = userService.registerUser(registerRequest);
        Reservation result = reservationService.createNewReservation(request, court, user);
        List<Reservation> reservations = reservationService.getAllReservationForUser(user);
        assertEquals(1, reservations.size());
        Reservation reservation = reservations.get(0);
        assertEquals(reservation.getCourt().getId(), court.getId());
        assertEquals(reservation.getStartTime().truncatedTo(ChronoUnit.SECONDS), request.getStartTime().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(reservation.getEndTime().truncatedTo(ChronoUnit.SECONDS), request.getStartTime().plusHours(request.getHoursOfGame()).truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    void createNewReservationWithEndTimeAfterCloseTime_returnFalse(){
        ReservationRequest request = ReservationRequest.builder()
                .startTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(22)
                        .withMinute(0)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        Court court = Court.builder()
                .name("CenterCourt")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://www.edwardssports.co.uk/pub/media/wysiwyg/tennis_court_dimensions_1_.jpg")
                .build();
        courtRepository.save(court);

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();
        User user = userService.registerUser(registerRequest);
        assertThrows(ReservationTimeException.class ,() -> reservationService.createNewReservation(request, court, user));
    }

    @Test
    void createNewReservationWithStartTimeAfter7Days_returnFalse(){
        ReservationRequest request = ReservationRequest.builder()
                .startTime(LocalDateTime.now()
                        .plusDays(10)
                        .withHour(13)
                        .withMinute(0)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        Court court = Court.builder()
                .name("CenterCourt")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://www.edwardssports.co.uk/pub/media/wysiwyg/tennis_court_dimensions_1_.jpg")
                .build();
        courtRepository.save(court);

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();
        User user = userService.registerUser(registerRequest);
        assertThrows(ReservationTimeException.class ,() -> reservationService.createNewReservation(request, court, user));
    }

    @Test
    void createNewReservationWithWalletStatusInactive_returnFalse(){
        ReservationRequest request = ReservationRequest.builder()
                .startTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(13)
                        .withMinute(0)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        Court court = Court.builder()
                .name("CenterCourt")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://www.edwardssports.co.uk/pub/media/wysiwyg/tennis_court_dimensions_1_.jpg")
                .build();
        courtRepository.save(court);

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();
        User user = userService.registerUser(registerRequest);
        user.setWallet(Wallet.builder()
                        .status(WalletStatus.INACTIVE)
                        .build());
        assertThrows(WalletInactiveException.class ,() -> reservationService.createNewReservation(request, court, user));
    }

    @Test
    void getReservationsForToday_happyPath(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();
        User user = userService.registerUser(registerRequest);

        user.getWallet().setBalance(BigDecimal.valueOf(100));
        walletRepository.save(user.getWallet());

        Court court = Court.builder()
                .name("CenterCourt")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://www.edwardssports.co.uk/pub/media/wysiwyg/tennis_court_dimensions_1_.jpg")
                .build();
        courtRepository.save(court);

        ReservationRequest request1 = ReservationRequest.builder()
                .startTime(LocalDateTime.now()
                        .withHour(16)
                        .withMinute(0)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        ReservationRequest request2 = ReservationRequest.builder()
                .startTime(LocalDateTime.now()
                        .withHour(17)
                        .withMinute(10)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        ReservationRequest request3 = ReservationRequest.builder()
                .startTime(LocalDateTime.now()
                        .withHour(19)
                        .withMinute(0)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        reservationService.createNewReservation(request1, court, user);
        reservationService.createNewReservation(request2, court, user);
        reservationService.createNewReservation(request3, court, user);

        List<Reservation> reservations = reservationService.getReservationsForToday(court);
        assertThat(reservations).hasSize(3);
    }

    @Test
    void getReservationsForNextSevenDays_happyPath(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .email("viktor@gmail.com")
                .password("123123")
                .confirmPassword("123123")
                .build();
        User user = userService.registerUser(registerRequest);

        Court court = Court.builder()
                .name("CenterCourt")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://www.edwardssports.co.uk/pub/media/wysiwyg/tennis_court_dimensions_1_.jpg")
                .build();
        courtRepository.save(court);

        LocalDateTime now = LocalDateTime.now();

        ReservationRequest request1 = ReservationRequest.builder()
                .startTime(now
                        .plusDays(10)
                        .withHour(16)
                        .withMinute(0)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        ReservationRequest request2 = ReservationRequest.builder()
                .startTime(now
                        .plusDays(3)
                        .withHour(17)
                        .withMinute(10)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        ReservationRequest request3 = ReservationRequest.builder()
                .startTime(now
                        .plusDays(4)
                        .withHour(19)
                        .withMinute(0)
                        .withSecond(0))
                .hoursOfGame(1)
                .build();

        reservationService.createNewReservation(request2, court, user);
        reservationService.createNewReservation(request3, court, user);

        Map<LocalDate, List<Reservation>> reservations = reservationService.getReservationsForNextSevenDays(court);
        assertEquals(2, reservations.size());
        assertTrue(reservations.containsKey(now.plusDays(3).toLocalDate()));
        assertTrue(reservations.containsKey(now.plusDays(4).toLocalDate()));
        assertThrows(ReservationTimeException.class, () -> reservationService.createNewReservation(request1, court, user));
        assertEquals(1, reservations.get(now.plusDays(3).toLocalDate()).size());
        assertEquals(1, reservations.get(now.plusDays(4).toLocalDate()).size());
    }
}
