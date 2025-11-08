package app.web;

import app.reservation.service.ReservationService;
import app.security.AuthenticationMetadata;
import app.training_session.dto.TrainingSession;
import app.training_session.service.TrainingSessionService;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/trainings")
public class TrainingSessionController {
    private final TrainingSessionService trainingSessionService;
    private final UserService userService;

    @Autowired
    public TrainingSessionController(TrainingSessionService trainingSessionService, UserService userService, ReservationService reservationService) {
        this.trainingSessionService = trainingSessionService;
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getTrainingSessionPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        ModelAndView modelAndView = new ModelAndView();
        UUID userId = authenticationMetadata.getUserId();
        List<TrainingSession> trainingsList = trainingSessionService.getAllTrainingsForUser(userId);
        modelAndView.addObject("trainings", trainingsList);
        modelAndView.setViewName("training-session");
        return modelAndView;
    }

    @PutMapping("/{id}")
    public String cancelTrainingSession(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        // trainingSession = trainingSessionService.getTrainingSessionById(id);
        User user = userService.getById(authenticationMetadata.getUserId());
        trainingSessionService.cancelSession(id);
        userService.getMoneyBackAfterCancelTraining(user);
        return "redirect:/trainings";
    }
}
