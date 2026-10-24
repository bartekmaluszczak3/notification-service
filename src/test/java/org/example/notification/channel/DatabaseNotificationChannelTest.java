package org.example.notification.channel;

import org.example.notification.channel.impl.DatabaseNotificationChannel;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.DeliveryStatus;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.Policy;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

public class DatabaseNotificationChannelTest extends IntegrationBase {

    @Autowired
    DatabaseNotificationChannel databaseNotificationChannel;

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
        DeliveryResult result = databaseNotificationChannel.send(notification, null);

        // then
        Assertions.assertEquals(DeliveryStatus.SUCCESS, result.deliveryStatus());
        var notificationEvent = getAllNotificationEvent().get(0);
        Assertions.assertEquals("notification-id", notificationEvent.get("notification_id"));
        Assertions.assertEquals("meter-id", notificationEvent.get("meter_id"));
        Assertions.assertEquals("anomaly-type", notificationEvent.get("anomaly_type"));
        Assertions.assertEquals("severity", notificationEvent.get("severity"));
        Assertions.assertEquals("description", notificationEvent.get("description"));
    }

    @Test
    void shouldReturnNullWhenResolvingTheReceipts(){
        // when
        var receipts = databaseNotificationChannel.resolveReceipts(new Policy());

        // then
        Assertions.assertNull(receipts);
    }
}
