package org.example.notification.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.notification.domain.policy.ChannelType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private String notificationId;
    private String meterId;
    private String eventId;
    private String anomalyType;
    private String severity;
    private String description;
    private BigDecimal detectedValue;
    private BigDecimal threshold;
    private Instant createdAt;
    private Set<ChannelType> channels;
    private Set<String> emailRecipients;
    private Set<String> smsRecipients;
    private Instant processedAt;
}
