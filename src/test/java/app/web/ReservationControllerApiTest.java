package app.web;

import app.court.model.Court;
import app.reservation.model.Reservation;
import app.reservation.model.ReservationStatus;
import app.reservation.repository.ReservationRepository;
import app.reservation.service.ReservationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static app.TestBuilder.aRandomUser;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
public class ReservationControllerApiTest {
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ReservationRepository reservationRepository;

    @MockitoBean
    private ReservationService reservationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRequestToMyReservationsPage() throws Exception {
        User user = aRandomUser();

        Court court = Court.builder()
                .name("CenterCourt")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://www.edwardssports.co.uk/pub/media/wysiwyg/tennis_court_dimensions_1_.jpg")
                .build();

        Reservation reservation1 = Reservation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .court(court)
                .startTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(12)
                        .withMinute(0)
                        .withSecond(0))
                .endTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(13)
                        .withMinute(0)
                        .withSecond(0))
                .totalPrice(BigDecimal.valueOf(15))
                .status(ReservationStatus.CONFIRMED)
                .build();

        Reservation reservation2 = Reservation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .court(court)
                .startTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(14)
                        .withMinute(0)
                        .withSecond(0))
                .endTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(15)
                        .withMinute(0)
                        .withSecond(0))
                .totalPrice(BigDecimal.valueOf(15))
                .status(ReservationStatus.CONFIRMED)
                .build();

        when(userService.getById(any())).thenReturn(user);
        when(reservationService.getAllReservationForUser(user)).thenReturn(List.of(reservation1, reservation2));
        MockHttpServletRequestBuilder request = get("/reservations/my")
                .with(user(new AuthenticationMetadata(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive())));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("my-reservations"))
                .andExpect(model().attributeExists("reservations"))
                .andExpect(model().attribute("reservations", hasSize(2)));

        verify(userService, times(1)).getById(any());
        verify(reservationService, times(1)).getAllReservationForUser(any());
    }

    @Test
    void getRequestToAllReservationsPage() throws Exception {
        User user = aRandomUser();

        Court court = Court.builder()
                .name("CenterCourt")
                .pricePerHour(BigDecimal.valueOf(15))
                .imageUrl("https://www.edwardssports.co.uk/pub/media/wysiwyg/tennis_court_dimensions_1_.jpg")
                .build();

        Reservation reservation1 = Reservation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .court(court)
                .startTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(12)
                        .withMinute(0)
                        .withSecond(0))
                .endTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(13)
                        .withMinute(0)
                        .withSecond(0))
                .totalPrice(BigDecimal.valueOf(15))
                .status(ReservationStatus.CONFIRMED)
                .build();

        Reservation reservation2 = Reservation.builder()
                .id(UUID.randomUUID())
                .user(user)
                .court(court)
                .startTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(14)
                        .withMinute(0)
                        .withSecond(0))
                .endTime(LocalDateTime.now()
                        .plusDays(1)
                        .withHour(15)
                        .withMinute(0)
                        .withSecond(0))
                .totalPrice(BigDecimal.valueOf(15))
                .status(ReservationStatus.CONFIRMED)
                .build();

        when(userService.getById(any())).thenReturn(user);
        when(reservationRepository.findAll()).thenReturn(List.of(reservation1, reservation2));

        MockHttpServletRequestBuilder request = get("/reservations/all")
                .with(user(new AuthenticationMetadata(user.getId(), user.getUsername(), user.getPassword(), UserRole.ADMIN, user.isActive())));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("reservations-all"))
                .andExpect(model().attributeExists("reservations"))
                .andExpect(model().attribute("reservations", hasSize(2)));

        verify(userService, times(1)).getById(any());
        verify(reservationRepository, times(1)).findAll();
    }
}
