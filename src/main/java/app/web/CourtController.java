package app.web;

import app.court.model.Court;
import app.court.service.CourtService;
import app.reservation.model.Reservation;
import app.reservation.service.ReservationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.ReservationRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/courts")
public class CourtController {

    private final UserService userService;
    private final CourtService courtService;
    private final ReservationService reservationService;

    @Autowired
    public CourtController(UserService userService, CourtService courtService, ReservationService reservationService) {
        this.userService = userService;
        this.courtService = courtService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public ModelAndView getAllCourts(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.getById(authenticationMetadata.getUserId());
        List<Court> courts = courtService.getAllCourts();

        Map<UUID, List<Reservation>> reservationMap = new HashMap<>();
        for(Court court:courts){
            List<Reservation> allReservationForToday = reservationService.getReservationsForToday(court);
            reservationMap.put(court.getId(), allReservationForToday);
        }
        modelAndView.setViewName("court-list");
        modelAndView.addObject("courts", courts);
        modelAndView.addObject("reservationsMap", reservationMap);
        return modelAndView;
    }

    @GetMapping("/{id}/reservations")
    public ModelAndView getReservationFormPage(@PathVariable UUID id, ReservationRequest reservationRequest, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reservation-form");
        Court court = courtService.findById(id);
        modelAndView.addObject("reservationRequest", reservationRequest);
        modelAndView.addObject("court", court);
        return modelAndView;
    }

    @PostMapping("/{id}/reservations")
    public ModelAndView processReservation(@PathVariable UUID id, @Valid ReservationRequest reservationRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        Court court = courtService.findById(id);

        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("reservationRequest", reservationRequest);
            modelAndView.addObject("court", court);
            modelAndView.setViewName("reservation-form");
            return modelAndView;
        }

        User user = userService.getById(authenticationMetadata.getUserId());
        boolean result = reservationService.createNewReservation(reservationRequest, court, user);

        if(!result){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("reservationRequest", reservationRequest);
            modelAndView.addObject("court", court);
            modelAndView.addObject("errorMessage", "The court is reserved at that time or the court center is closed!");
            modelAndView.setViewName("reservation-form");
            return modelAndView;
        }

        return new ModelAndView("redirect:/courts");
    }

    @GetMapping("/{id}/reservations/upcoming")
    public ModelAndView getReservationsForNextSevenDays(@PathVariable UUID id){
        ModelAndView modelAndView = new ModelAndView();
        Court court = courtService.findById(id);
        Map<LocalDate, List<Reservation>> reservations = reservationService.getReservationsForNextSevenDays(court);
        modelAndView.addObject("reservations", reservations);
        modelAndView.setViewName("reservation-list");
        return modelAndView;
    }
}
