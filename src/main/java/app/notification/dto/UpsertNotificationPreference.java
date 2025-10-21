package app.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpsertNotificationPreference {

    private UUID userId;

    private boolean reservationEnabled;

    private boolean productEnabled;

    private String contactInfo;
}
