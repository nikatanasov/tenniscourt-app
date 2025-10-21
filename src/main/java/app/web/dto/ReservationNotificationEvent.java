package app.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ReservationNotificationEvent {

    private UUID userId;

    private String email;

    private BigDecimal totalPrice;

    private String courtName;
}
