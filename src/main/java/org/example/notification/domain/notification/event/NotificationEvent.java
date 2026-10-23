package org.example.notification.domain.notification.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String notificationId;
    private String meterId;
    private String eventId;
    private String anomalyType;
    private String severity;
    private String description;
    private BigDecimal detectedValue;
    private BigDecimal threshold;
    private OffsetDateTime detectedAt;
    private OffsetDateTime processedAt;
}
