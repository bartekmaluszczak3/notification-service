package org.example.notification.service.dispatcher;

import lombok.SneakyThrows;
import org.example.notification.channel.NotificationChannel;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.service.notification.DeliveryDispatcher;
import org.example.notification.service.notification.event.NotificationEventService;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Import(DeliveryDispatcherTestBase.NotificationChannelTestConfig.class)
@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
public class DispatcherE2ETest extends IntegrationBase {

    @Autowired
    protected DeliveryDispatcher dispatcher;

    @Autowired
    protected NotificationEventService notificationEventService;

    @Autowired
    @Qualifier("emailNotificationChannel")
    protected NotificationChannel emailChannel;

    @Autowired
    @Qualifier("smsNotificationChannel")
    protected NotificationChannel smsChannel;

    @BeforeEach
    void setUp() {
        reset(emailChannel, smsChannel);
        when(emailChannel.type()).thenReturn(ChannelType.EMAIL);
        when(smsChannel.type()).thenReturn(ChannelType.SMS);
    }

    @Test
    void shouldSendEmailAndPersistDeliveryResultInDatabase() throws Exception {
        // given
        Notification notification = createNotificationEvent(Set.of(ChannelType.EMAIL));
        when(emailChannel.resolveReceipts(notification))
                .thenReturn(List.of("test@example.com"));

        when(emailChannel.send(notification, "test@example.com"))
                .thenReturn(DeliveryResult.success());

        // when
        dispatcher.dispatch(notification);
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(emailChannel, times(1))
                            .send(notification, "test@example.com");
                });

        // then
        var deliveryLogs = getAllNotificationDeliveryLog();
        Assertions.assertEquals(1, deliveryLogs.size());
        var delivery = deliveryLogs.get(0);
        Assertions.assertEquals("notification-id", delivery.get("notification_id"));
        Assertions.assertEquals("EMAIL", delivery.get("channel"));
        Assertions.assertEquals("test@example.com", delivery.get("recipient"));
        Assertions.assertEquals("SUCCESS", delivery.get("status"));
        Assertions.assertEquals(0, delivery.get("retry_count"));
    }

    @Test
    void shouldRetryFailedEmailDelivery() throws Exception {
        // given
        Notification notification = createNotificationEvent(Set.of(ChannelType.EMAIL));
        when(emailChannel.resolveReceipts(notification))
                .thenReturn(List.of("test@example.com"));

        when(emailChannel.send(notification, "test@example.com"))
                .thenReturn(
                        DeliveryResult.failed("error"),
                        DeliveryResult.failed("error"),
                        DeliveryResult.success()
                );

        // when
        dispatcher.dispatch(notification);
        await()
                .atMost(Duration.ofSeconds(20))
                .untilAsserted(() -> {
                    verify(emailChannel, times(3))
                            .send(notification, "test@example.com");
                });

        // then
        var deliveryLogs = getAllNotificationDeliveryLog();
        Assertions.assertEquals(1, deliveryLogs.size());
        var delivery = deliveryLogs.get(0);
        Assertions.assertEquals("notification-id", delivery.get("notification_id"));
        Assertions.assertEquals("EMAIL", delivery.get("channel"));
        Assertions.assertEquals("test@example.com", delivery.get("recipient"));
        Assertions.assertEquals("SUCCESS", delivery.get("status"));
        Assertions.assertEquals(2, delivery.get("retry_count"));
    }

    @Test
    void shouldStopRetryingAfterMaxAttempts() throws Exception {
        // given
        Notification notification = createNotificationEvent(Set.of(ChannelType.EMAIL));
        when(emailChannel.resolveReceipts(notification))
                .thenReturn(List.of("test@example.com"));

        when(emailChannel.send(notification, "test@example.com"))
                .thenReturn(
                        DeliveryResult.failed("error"),
                        DeliveryResult.failed("error"),
                        DeliveryResult.failed("error")
                );

        // when
        dispatcher.dispatch(notification);
        await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    verify(emailChannel, times(3))
                            .send(notification, "test@example.com");
                });

        // then
        var deliveryLogs = getAllNotificationDeliveryLog();
        Assertions.assertEquals(1, deliveryLogs.size());
        var delivery = deliveryLogs.get(0);
        Assertions.assertEquals("FAILED", delivery.get("status"));
        Assertions.assertEquals(2, delivery.get("retry_count"));
    }

    @Test
    void shouldSendEmailAndSmsToAllRecipients() {
        // given
        Notification notification = createNotificationEvent(Set.of(ChannelType.SMS, ChannelType.EMAIL));

        List<String> emailRecipients = List.of(
                "first@example.com",
                "second@example.com"
        );
        List<String> smsRecipients = List.of(
                "+48111111111",
                "+48222222222"
        );
        when(emailChannel.resolveReceipts(notification))
                .thenReturn(emailRecipients);

        when(smsChannel.resolveReceipts(notification))
                .thenReturn(smsRecipients);

        when(emailChannel.send(eq(notification), anyString()))
                .thenReturn(DeliveryResult.success());

        when(smsChannel.send(eq(notification), anyString()))
                .thenReturn(DeliveryResult.success());

        // when
        dispatcher.dispatch(notification);

        // then
        await()
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(emailChannel, times(2))
                            .send(eq(notification), anyString());

                    verify(smsChannel, times(2))
                            .send(eq(notification), anyString());
                });


        // then
        var deliveryLogs = getAllNotificationDeliveryLog();
        Assertions.assertEquals(4, deliveryLogs.size());
    }

    @SneakyThrows
    private Notification createNotificationEvent(Set<ChannelType> channelTypes) {
        var notification = Notification.builder()
                .notificationId("notification-id")
                .meterId("meter-id")
                .eventId("event-id")
                .anomalyType("anomaly-type")
                .severity("severity")
                .description("description")
                .threshold(BigDecimal.ONE)
                .channels(channelTypes)
                .detectedValue(BigDecimal.TEN)
                .createdAt(Instant.now())
                .processedAt(Instant.now())
                .build();
        notificationEventService.save(notification);
        return notification;
    }
}
