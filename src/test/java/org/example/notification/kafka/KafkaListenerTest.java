package org.example.notification.kafka;

import lombok.SneakyThrows;
import org.example.device.events.AnomalyDetectedEvent;
import org.example.device.events.AnomalyType;
import org.example.device.events.Severity;
import org.example.notification.channel.NotificationChannel;
import org.example.notification.domain.notification.DeliveryResult;
import org.example.notification.domain.notification.Notification;
import org.example.notification.domain.policy.ChannelType;
import org.example.notification.domain.policy.dto.PolicyRequest;
import org.example.notification.service.dispatcher.DeliveryDispatcherTestBase;
import org.example.notification.service.policy.PolicyService;
import org.example.notification.shared.IntegrationBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Import(DeliveryDispatcherTestBase.NotificationChannelTestConfig.class)
@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true"
})
public class KafkaListenerTest extends IntegrationBase {

    @Autowired
    protected KafkaTemplate<String, AnomalyDetectedEvent> kafkaTemplate;

    @Autowired
    protected PolicyService policyService;

    @Autowired
    @Qualifier("emailNotificationChannel")
    protected NotificationChannel emailChannel;

    @Autowired
    @Qualifier("smsNotificationChannel")
    protected NotificationChannel smsChannel;

    @SneakyThrows
    @Test
    void shouldConsumeKafkaEventAndDispatchToAllRecipients() {
        List<String> emailRecipients = List.of(
                "first@example.com",
                "second@example.com"
        );

        List<String> smsRecipients = List.of(
                "+48111111111",
                "+48222222222"
        );
        createPolicy();
        AnomalyDetectedEvent event = createAnomalyEvent();
        when(emailChannel.resolveReceipts(any(Notification.class)))
                .thenReturn(emailRecipients);

        when(smsChannel.resolveReceipts(any(Notification.class)))
                .thenReturn(smsRecipients);

        when(emailChannel.send(any(Notification.class), anyString()))
                .thenReturn(DeliveryResult.success());

        when(smsChannel.send(any(Notification.class), anyString()))
                .thenReturn(DeliveryResult.success());

        // when
        kafkaTemplate.send("anomaly.detected", "meter-id", event)
                .get(10, TimeUnit.SECONDS);
        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    List<Map<String, Object>> events = getAllNotificationEvent();

                    Assertions.assertEquals(1, events.size());
                    var notification = events.get(0);
                    Assertions.assertEquals("meter-001", notification.get("meter_id"));
                    Assertions.assertEquals("OVERVOLTAGE", notification.get("anomaly_type"));
                    Assertions.assertEquals("CRITICAL", notification.get("severity"));
                });

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    verify(emailChannel, times(2))
                            .send(any(Notification.class), anyString());

                    verify(smsChannel, times(2))
                            .send(any(Notification.class), anyString());
                });
        var notificationDeliveryLog = getAllNotificationDeliveryLog();
        Assertions.assertEquals(4, notificationDeliveryLog.size());
        notificationDeliveryLog.forEach(notification -> {
            Assertions.assertEquals("SUCCESS", notification.get("status"));

        });
    }

    @SneakyThrows
    @Test
    void shouldUseDefaultPolicyAndOnlySaveInDatabase() {
        //
        AnomalyDetectedEvent event = createAnomalyEvent();

        // when
        kafkaTemplate.send("anomaly.detected", "meter-id", event)
                .get(10, TimeUnit.SECONDS);

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    List<Map<String, Object>> events = getAllNotificationEvent();

                    Assertions.assertEquals(1, events.size());
                    var notification = events.get(0);
                    Assertions.assertEquals("meter-001", notification.get("meter_id"));
                    Assertions.assertEquals("OVERVOLTAGE", notification.get("anomaly_type"));
                    Assertions.assertEquals("CRITICAL", notification.get("severity"));
                });

        await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    verify(emailChannel, never())
                            .send(any(Notification.class), anyString());

                    verify(smsChannel, never())
                            .send(any(Notification.class), anyString());
                });

    }

    private AnomalyDetectedEvent createAnomalyEvent() {
        AnomalyDetectedEvent anomalyDetectedEvent = new AnomalyDetectedEvent();
        anomalyDetectedEvent.setMeterId("meter-001");
        anomalyDetectedEvent.setAnomalyType(AnomalyType.OVERVOLTAGE);
        anomalyDetectedEvent.setSeverity(Severity.CRITICAL);
        anomalyDetectedEvent.setDescription("desc");
        anomalyDetectedEvent.setDetectedValue(10.0);
        anomalyDetectedEvent.setThreshold(12.0);
        anomalyDetectedEvent.setEventId(UUID.randomUUID().toString());
        return anomalyDetectedEvent;
    }

    @SneakyThrows
    private void createPolicy() {
        policyService.save(PolicyRequest.builder()
                .anomalyType("OVERVOLTAGE")
                .severity("CRITICAL")
                .channelTypes(Set.of(ChannelType.EMAIL, ChannelType.SMS))
                .meterId("meter-001")
                .build());
    }
}
