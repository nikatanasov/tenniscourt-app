package app.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewReviewRequest {

    @NotBlank
    private String courtName;

    @Positive
    @Min(1)
    @Max(6)
    private int rating;

    private String comment;

    @NotBlank
    private String username;
}
