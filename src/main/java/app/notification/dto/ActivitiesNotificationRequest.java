package app.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ActivitiesNotificationRequest {
    private UUID userId;

    private String subject;

    private String message;

    private String type;
}
