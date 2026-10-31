package org.example.notification.service.notification.event;

import lombok.SneakyThrows;
import org.example.notification.domain.notification.Notification;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

public class NotificationEventServiceTest  extends IntegrationBase {
    @Autowired
    NotificationEventService notificationEventService;

    @SneakyThrows
    @Test
    void shouldCreateNotificationEvent() {
        // given
        var notification = Notification.builder()
                .notificationId("notification-id")
                .meterId("meter-id")
                .eventId("event-id")
                .anomalyType("anomaly-type")
                .severity("severity")
                .description("description")
                .threshold(BigDecimal.ONE)
                .detectedValue(BigDecimal.TEN)
                .createdAt(Instant.now())
                .processedAt(Instant.now())
                .build();

        // when
        notificationEventService.save(notification);

        // then
        var notificationEvent = getAllNotificationEvent().get(0);
        Assertions.assertEquals("notification-id", notificationEvent.get("notification_id"));
        Assertions.assertEquals("meter-id", notificationEvent.get("meter_id"));
        Assertions.assertEquals("anomaly-type", notificationEvent.get("anomaly_type"));
        Assertions.assertEquals("severity", notificationEvent.get("severity"));
        Assertions.assertEquals("description", notificationEvent.get("description"));
    }
}
