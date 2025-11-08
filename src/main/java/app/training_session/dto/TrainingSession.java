package app.training_session.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TrainingSession {
    private UUID id;

    private String courtName;

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
