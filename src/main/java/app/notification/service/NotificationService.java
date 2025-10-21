package app.notification.service;

import app.notification.client.NotificationClient;
import app.notification.dto.ActivitiesNotificationRequest;
import app.notification.dto.ActivityNotification;
import app.notification.dto.NotificationPreference;
import app.notification.dto.UpsertNotificationPreference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {
    private final NotificationClient notificationClient;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void saveNotificationPreference(UUID userId, boolean reservationEnabled, boolean productEnabled, String contactInfo){
        UpsertNotificationPreference upsertNotificationPreference = UpsertNotificationPreference.builder()
                .userId(userId)
                .reservationEnabled(reservationEnabled)
                .productEnabled(productEnabled)
                .contactInfo(contactInfo)
                .build();

        //тук изпращам заявка към микросървиса
        ResponseEntity<Void> httpResponse = notificationClient.upsertNotificationPreference(upsertNotificationPreference);
        if(!httpResponse.getStatusCode().is2xxSuccessful()){
            log.error("Can't save preference for user with id "+userId+"!");
        }
    }

    public NotificationPreference getNotificationPreferencePage(UUID userId) {
        ResponseEntity<NotificationPreference> httpResponse = notificationClient.getNotificationPreference(userId);

        if(!httpResponse.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException("Notification preference for user with id "+userId+" does not exist!");
        }
        return httpResponse.getBody();
    }

    public List<ActivityNotification> getNotificationHistory(UUID userId) {
        ResponseEntity<List<ActivityNotification>> httpResponse = notificationClient.getNotificationHistory(userId);
        return httpResponse.getBody();
    }

    public void sendNotification(UUID userId, String subject, String message, String type){
        ActivitiesNotificationRequest request = ActivitiesNotificationRequest.builder()
                .userId(userId)
                .subject(subject)
                .message(message)
                .type(type)
                .build();
        try {
            ResponseEntity<Void> httpResponse = notificationClient.sendNotification(request);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("Can't send email to user with id " + userId + "!");
            }
        }catch (Exception e){
            log.info("Reservation or products notifications not enabled for user with id "+userId+"!");
        }
    }

    public void clearAllNotifications(UUID userId){
        ResponseEntity<Void> httpResponse = notificationClient.clearAllNotifications(userId);
    }

    public void showAllNotifications(UUID userId){
        ResponseEntity<Void> httpResponse = notificationClient.showAllNotifications(userId);
    }
}
