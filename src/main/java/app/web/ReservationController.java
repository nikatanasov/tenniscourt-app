package app.web;

import app.reservation.model.Reservation;
import app.reservation.repository.ReservationRepository;
import app.reservation.service.ReservationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final UserService userService;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(UserService userService, ReservationRepository reservationRepository, ReservationService reservationService) {
        this.userService = userService;
        this.reservationRepository = reservationRepository;
        this.reservationService = reservationService;
    }

    @GetMapping("/me")
    public ModelAndView getMyReservationsPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Reservation> reservations = reservationService.getAllReservationForUser(user);
        modelAndView.setViewName("my-reservations");
        modelAndView.addObject("reservations", reservations);
        return modelAndView;
    }

    @GetMapping("/all")//samo za admini
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllReservations(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Reservation> reservations = reservationRepository.findAll();
        modelAndView.setViewName("reservations-all");
        modelAndView.addObject("reservations", reservations);
        return modelAndView;
    }


}
