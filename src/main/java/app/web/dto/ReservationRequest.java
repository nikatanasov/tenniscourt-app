package app.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.lang.annotation.After;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {

    @Future(message = "Началният час трябва да е в бъдещето")
    @NotNull(message = "Началният час е задължителен")
    private LocalDateTime startTime;

    @Positive
    @Min(1)
    @Max(4)
    private int hoursOfGame;

    private boolean training;
}
