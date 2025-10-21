package app.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActivityNotification {
    private String subject;

    private String type;

    private String status;

    private LocalDateTime createdOn;
}
