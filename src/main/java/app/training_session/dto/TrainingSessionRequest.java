package app.training_session.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TrainingSessionRequest {
    private UUID userId;

    private String courtName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
