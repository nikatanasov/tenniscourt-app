package app.notification.client;

import app.notification.dto.ActivitiesNotificationRequest;
import app.notification.dto.ActivityNotification;
import app.notification.dto.NotificationPreference;
import app.notification.dto.UpsertNotificationPreference;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "activity-svc", url = "http://localhost:8081/api/v1/notifications")
public interface NotificationClient {

    @PostMapping("/preferences")
    ResponseEntity<Void> upsertNotificationPreference(@RequestBody UpsertNotificationPreference upsertNotificationPreference);

    @GetMapping("/preferences")
    ResponseEntity<NotificationPreference> getNotificationPreference(@RequestParam(name = "userId") UUID userId);

    @GetMapping
    ResponseEntity<List<ActivityNotification>> getNotificationHistory(@RequestParam(name = "userId") UUID userId);

    @PostMapping
    ResponseEntity<Void> sendNotification(@RequestBody ActivitiesNotificationRequest activitiesNotificationRequest);

    @PutMapping("/clear")
    ResponseEntity<Void> clearAllNotifications(@RequestParam(name = "userId") UUID userId);

    @PutMapping("/show")
    ResponseEntity<Void> showAllNotifications(@RequestParam(name = "userId") UUID userId);
}
