package app.web;

import app.notification.dto.ActivityNotification;
import app.notification.dto.NotificationPreference;
import app.notification.service.NotificationService;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/preferences")
    public ModelAndView getNotificationPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        ModelAndView modelAndView = new ModelAndView();
        UUID userId = authenticationMetadata.getUserId();
        User user = userService.getById(userId);
        NotificationPreference notificationPreference = notificationService.getNotificationPreferencePage(userId);
        List<ActivityNotification> notificationHistory = notificationService.getNotificationHistory(user.getId());
        modelAndView.addObject("notificationPreference", notificationPreference);
        modelAndView.addObject("notificationHistory", notificationHistory);
        modelAndView.setViewName("notification-page");
        return modelAndView;
    }

    //Trqbva da opravq tezi butoni
    @PutMapping("/preferences/reservations")
    public String changeReservationEnabled(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        UUID userId = authenticationMetadata.getUserId();
        NotificationPreference preference = notificationService.getNotificationPreferencePage(userId);
        notificationService.saveNotificationPreference(userId, !preference.isReservationEnabled(), preference.isProductEnabled(), preference.getContactInfo());
        return "redirect:/notifications/preferences";
    }

    @PutMapping("/preferences/products")
    public String changeProductEnabled(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        UUID userId = authenticationMetadata.getUserId();
        NotificationPreference preference = notificationService.getNotificationPreferencePage(userId);
        notificationService.saveNotificationPreference(userId, preference.isReservationEnabled(), !preference.isProductEnabled(), preference.getContactInfo());
        return "redirect:/notifications/preferences";
    }

    @PutMapping("/clear")
    public String clearAllNotifications(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        UUID userId = authenticationMetadata.getUserId();
        notificationService.clearAllNotifications(userId);
        return "redirect:/notifications/preferences";
    }

    @PutMapping("/show")
    public String showAllNotifications(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        UUID userId = authenticationMetadata.getUserId();
        notificationService.showAllNotifications(userId);
        return "redirect:/notifications/preferences";
    }



}
