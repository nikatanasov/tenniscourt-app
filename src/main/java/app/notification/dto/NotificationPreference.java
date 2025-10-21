package app.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NotificationPreference {

    private boolean reservationEnabled;

    private boolean productEnabled;

    private String contactInfo;
}
