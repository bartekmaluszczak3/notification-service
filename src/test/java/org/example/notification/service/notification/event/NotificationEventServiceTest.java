package org.example.notification.service.notification.event;

import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.notification.event.NotificationEvent;
import org.example.notification.exception.DatabaseException;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

public class NotificationEventServiceTest extends IntegrationBase {

    @Autowired
    NotificationEventService notificationEventService;

    @Test
    void shouldCreateNotificationEvent() throws DatabaseException {
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
        NotificationEvent notificationEvent = notificationEventService.create(notification);

        // then
        Assertions.assertEquals("notification-id", notificationEvent.getNotificationId());
        Assertions.assertEquals("meter-id", notificationEvent.getMeterId());
        Assertions.assertEquals("event-id", notificationEvent.getEventId());
        Assertions.assertEquals("anomaly-type", notificationEvent.getAnomalyType());
        Assertions.assertEquals("severity", notificationEvent.getSeverity());
        Assertions.assertEquals("description", notificationEvent.getDescription());
    }
}
