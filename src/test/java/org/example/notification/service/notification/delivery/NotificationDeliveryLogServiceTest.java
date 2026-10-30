package org.example.notification.service.notification.delivery;

import lombok.SneakyThrows;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.exception.DatabaseException;
import org.example.notification.repository.notification.event.NotificationEventRepository;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;

public class NotificationDeliveryLogServiceTest extends IntegrationBase {

    @Autowired
    NotificationEventRepository eventRepository;
    @Autowired
    NotificationDeliveryLogService service;

    @Test
    @SneakyThrows
    void shouldCreateNotificationDeliveryLog() {
        // given
        createNotificationEvent();

        // when
        var optionalNotification = service.insertPending("notification-id", ChannelType.SMS, "123456789");

        // then
        Assertions.assertTrue(optionalNotification.isPresent());
        var notifications = getNotificationByDeliveryId(optionalNotification.get());
        Assertions.assertEquals(1, notifications.size());
        var notificationLog = notifications.get(0);
        Assertions.assertEquals("notification-id", notificationLog.get("notification_id"));
        Assertions.assertEquals(ChannelType.SMS.name(), notificationLog.get("channel"));
        Assertions.assertEquals("123456789", notificationLog.get("recipient"));
        Assertions.assertEquals(0, notificationLog.get("retry_count"));
    }
    @Test
    @SneakyThrows
    void shouldUpdateNotificationDeliveryLog() {
        // given
        createNotificationEvent();
        var deliveryId = service.insertPending("notification-id", ChannelType.SMS, "123456789").get();

        // when
        service.updateResult(deliveryId, DeliveryResult.failed("failed"), 2);

        // then
        var notification = getNotificationByDeliveryId(deliveryId).get(0);
        Assertions.assertEquals("FAILED", notification.get("status"));
        Assertions.assertEquals(1, notification.get("retry_count"));
    }


    private void createNotificationEvent() throws DatabaseException {
        // given
        Notification notification = Notification.builder()
                .notificationId("notification-id")
                .meterId("meter-id")
                .eventId("event-id")
                .anomalyType("anomaly-type")
                .severity("severity")
                .description("description")
                .detectedValue(new BigDecimal("253.75"))
                .processedAt(Instant.now())
                .build();
        eventRepository.save(notification);
    }
}
